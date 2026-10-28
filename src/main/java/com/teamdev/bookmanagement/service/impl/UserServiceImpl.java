package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.common.CacheKeys;
import com.teamdev.bookmanagement.common.StatItem;
import com.teamdev.bookmanagement.common.TrendPoint;
import com.teamdev.bookmanagement.dto.request.*;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
import com.teamdev.bookmanagement.dto.response.UserStatsResponse;
import com.teamdev.bookmanagement.entity.SuperAdminTransfer;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.SuperAdminTransferMapper;
import com.teamdev.bookmanagement.mapper.UserMapper;
import com.teamdev.bookmanagement.service.MailService;
import com.teamdev.bookmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户业务实现。
 * ServiceImpl<Mapper, Entity> 是 MyBatis-Plus 提供的通用实现,和上面的 IService 配套。
 * 后面要加自定义业务逻辑(比如登录校验),就在这里补方法。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.user_password}")
    private String resetUserPassword;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final SuperAdminTransferMapper superAdminTransferMapper;
    private final MailService mailService;
    private final UserMapper userMapper;
    private static final int MAX_FAIL_COUNT=5;
    private static final int LOCK_MINUTES=5;

    @Override
    public void register(RegisterRequest registerRequest,int role) {
        if(role==2){
            throw new BusinessException(403,"无创建超级管理员的权限");
        }
        String captchaKey= CacheKeys.CAPTCHA_PREFIX+registerRequest.getCaptchaId();
        String captchaCode=stringRedisTemplate.opsForValue().get(captchaKey);
        stringRedisTemplate.delete(captchaKey);
        if (captchaCode==null||!captchaCode.equalsIgnoreCase(registerRequest.getCaptchaCode())){
            throw new BusinessException(400,"验证码错误或过期");
        }
        if (adminUsername.equals(registerRequest.getUsername())||lambdaQuery().eq(User::getUsername,registerRequest.getUsername()).exists()){
            throw new BusinessException(400,"用户名已存在");
        }
        String email=registerRequest.getEmail();
        if (email!=null&&email.isBlank()){
            email=null;
        }
        if (email!=null){
            if (lambdaQuery().eq(User::getEmail,email).exists()) {
                throw new BusinessException(400, "邮箱已被注册");
            }
            doCheckEmailCode(registerRequest.getEmailCode(), registerRequest.getEmail(), CacheKeys.EMAIL_BINDING_CODE_PREFIX, CacheKeys.EMAIL_BINDING_FAIL_PREFIX);
        }
        User user=User.builder().username(registerRequest.getUsername()).password(passwordEncoder.encode(registerRequest.getPassword())).role(role).status(1).email(email).build();
        save(user);
        log.info("{}{}注册成功",role==0?"用户":"管理员",user.getUsername());
    }

    @Override
    public Boolean deleteById(Long id){
        User adminUser=getOptById(StpUtil.getLoginIdAsLong()).orElseThrow(()->new BusinessException(404,"进行删除用户操作的用户不存在"));
        User user=getOptById(id).orElseThrow(()->new BusinessException(404,"要删除的用户不存在"));
        if(adminUser.getRole()<=user.getRole()){
            throw new BusinessException(403,"无权限删除此用户");
        }
        boolean success=removeById(id);
        StpUtil.kickout(id);
        log.info("用户{}删除用户{}{}",adminUser.getUsername(),user.getUsername(),success?"成功":"失败");
        return success;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String captchaKey= CacheKeys.CAPTCHA_PREFIX+loginRequest.getCaptchaId();
        String captchaCode=stringRedisTemplate.opsForValue().get(captchaKey);
        stringRedisTemplate.delete(captchaKey);
        if (captchaCode==null||!captchaCode.equalsIgnoreCase(loginRequest.getCaptchaCode())){
            throw new BusinessException(400,"验证码错误或过期");
        }
        User user=lambdaQuery().eq(User::getUsername,loginRequest.getUsername()).one();
        if (user==null) {
            throw new BusinessException(400, "用户名或密码错误");
        }
        if (user.getStatus()!=null&&user.getStatus()==0){
            throw new BusinessException(403,"账号已禁用");
        }
        String failKey="login:fail:"+loginRequest.getUsername();
        String failCountString=stringRedisTemplate.opsForValue().get(failKey);
        int failCount=failCountString==null?0:Integer.parseInt(failCountString);
        if (failCount>=MAX_FAIL_COUNT){
            log.warn("用户{}登录失败次数过多，锁定5分钟",user.getUsername());
            throw new BusinessException(429,"失败次数过多，请待五分钟后重试");
        }
        if (loginRequest.getPassword()==null||loginRequest.getPassword().isBlank()||!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey,LOCK_MINUTES, TimeUnit.MINUTES);
            throw new BusinessException(400,"用户名或密码错误");
        }
        StpUtil.login(user.getId());
        stringRedisTemplate.delete(failKey);
        log.info("用户{}登录成功",user.getUsername());
        return LoginResponse.builder().avatar(user.getAvatar()).id(user.getId()).username(user.getUsername()).role(user.getRole()).token(StpUtil.getTokenValue()).email(user.getEmail()).build();
    }

    @Override
    public LoginResponse emailLogin(EmailLoginRequest emailLoginRequest){
        String email=emailLoginRequest.getEmail();
        User user=lambdaQuery().eq(User::getEmail,email).one();
        if (user==null){
            throw new BusinessException(404,"邮箱地址或密码错误");
        }
        return login(LoginRequest.builder().captchaCode(emailLoginRequest.getCaptchaCode()).captchaId(emailLoginRequest.getCaptchaId()).password(emailLoginRequest.getPassword()).username(user.getUsername()).build());
    }

    @Override
    public Boolean updateUserName(UpdateUserNameRequest updateUserNameRequest) {
        if (updateUserNameRequest.getUsername() != null && !updateUserNameRequest.getUsername().isBlank() && lambdaQuery().eq(User::getUsername, updateUserNameRequest.getUsername()).ne(User::getId, StpUtil.getLoginIdAsLong()).exists()) {
            throw new BusinessException(400, "用户已存在");
        }
        User user=lambdaQuery().eq(User::getId,StpUtil.getLoginIdAsLong()).one();
        if (user==null){
            throw new BusinessException(400,"更改用户名操作的用户未找到");
        }
        boolean success=update(new LambdaUpdateWrapper<User>().set(User::getUsername,updateUserNameRequest.getUsername()).eq(User::getId,StpUtil.getLoginIdAsLong()));
        if (success){
            log.info("用户{}更改用户名为{}成功",user.getUsername(),updateUserNameRequest.getUsername());
        }
        else {
            log.warn("用户{}更改用户名失败",user.getUsername());
        }
        return success;
    }

    @Override
    public Boolean updateUserPassword(UpdateUserPasswordRequest updateUserPasswordRequest){
        User user=lambdaQuery().eq(User::getId,StpUtil.getLoginIdAsLong()).one();
        if (user==null){
            throw new BusinessException(400,"更改密码操作的用户未找到");
        }
        if (updateUserPasswordRequest.getNewPassword() == null|| updateUserPasswordRequest.getNewPassword().isBlank()|| updateUserPasswordRequest.getOldPassword()==null|| updateUserPasswordRequest.getOldPassword().isBlank()) {
            throw new BusinessException(400,"更改密码操作的密码格式错误");
        }
        if (!passwordEncoder.matches(updateUserPasswordRequest.getOldPassword(), user.getPassword())){
            throw new BusinessException(400,"旧密码验证错误");
        }
        User newUser= User.builder().password(passwordEncoder.encode(updateUserPasswordRequest.getNewPassword())).role(user.getRole()).status(user.getStatus()).id(user.getId()).username(user.getUsername()).build();
        boolean success=updateById(newUser);
        if (success){
            log.info("用户{}修改密码成功",newUser.getUsername());
        }
        else {
            log.warn("用户{}修改密码失败",newUser.getUsername());
        }
        return success;
    }

    @Override
    public Boolean helpResetPassword(Long id){
        if (resetUserPassword==null||resetUserPassword.isBlank()){
            log.warn("未设置重置后密码，重置失败");
            throw new BusinessException(404,"未设置重置后密码");
        }
        User user=getOptById(id).orElseThrow(()->new BusinessException(404,"重置密码的用户不存在"));
        if (user.getRole().equals(2)){
            log.warn("重置密码操作无权限");
            throw new BusinessException(403,"无权限");
        }
        User actionUser=getOptById(StpUtil.getLoginIdAsLong()).orElseThrow(()->new BusinessException(404,"进行密码重置操作的用户不存在"));
        if (user.getRole().equals(1)){
            if (!actionUser.getRole().equals(2)){
                throw new BusinessException(403,"无权限");
            }
        }
        boolean success=lambdaUpdate().set(User::getPassword,passwordEncoder.encode(resetUserPassword)).eq(User::getId,id).update();
        log.info("管理员[{}]重置用户[{}]“{}”的密码{}",StpUtil.getLoginIdAsLong(),id,user.getUsername(),success?"成功":"失败");
        if (success){
            StpUtil.kickout(id);
        }
        return success;
    }

    @Override
    public UserResponse getById(Long id){
        User user=lambdaQuery().eq(User::getId,id).one();
        if (user==null){
            throw new BusinessException(404,"用户不存在");
        }
        return toResponse(user);
    }

    @Override
    public List<UserResponse> listUsers(){
        return list().stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user){
        return UserResponse.builder().avatar(user.getAvatar()).id(user.getId()).username(user.getUsername()).status(user.getStatus()).role(user.getRole()).createTime(user.getCreateTime()).updateTime(user.getUpdateTime()).email(user.getEmail()).build();
    }

    @Override
    public Boolean updateUserStatus(Long id, UpdateUserStatusRequest updateUserStatusRequest){
        User adminUser=getOptById(StpUtil.getLoginIdAsLong()).orElseThrow(()->new BusinessException(404,"进行状态更新的管理员用户不存在"));
        if (updateUserStatusRequest.getStatus()==null || updateUserStatusRequest.getStatus()!=0&&updateUserStatusRequest.getStatus()!=1){
            throw new BusinessException(400,"更新用户状态传入参数非法");
        }
        User user=lambdaQuery().eq(User::getId,id).one();
        if (user==null){
            throw new BusinessException(404,"用户不存在");
        }
        if (id==StpUtil.getLoginIdAsLong()){
            throw new BusinessException(400,"管理员不能操作自己");
        }
        if(adminUser.getRole()<=user.getRole()){
            throw new BusinessException(403,"状态更新操作无权限");
        }
        boolean success=update(new LambdaUpdateWrapper<User>().set(User::getStatus,updateUserStatusRequest.getStatus()).eq(User::getId,id));
        if (success){
            if (updateUserStatusRequest.getStatus()==0) {
                StpUtil.kickout(id);
            }
            log.info("管理员{}将用户{}账号{}成功",adminUser.getUsername(),user.getUsername(),updateUserStatusRequest.getStatus()==0?"冻结":"启用");
        }
        return success;
    }

    @Override
    public Boolean updateUserRole(Long id, UpdateUserRoleRequest request){
        User user=lambdaQuery().eq(User::getId,id).one();
        if (user==null){
            throw new BusinessException(404,"用户不存在");
        }
        if (id==StpUtil.getLoginIdAsLong()){
            throw new BusinessException(400,"管理员不能操作自己");
        }
        // role=2 表示发起超级管理员转让,不是立即改角色
        if (request.getRole()==2){
            // 禁止重复发起:已存在待生效(status=0)的转让时拒绝
            Long pendingCount = superAdminTransferMapper.selectCount(
                    new LambdaQueryWrapper<SuperAdminTransfer>().eq(SuperAdminTransfer::getStatus, 0)
            );
            if (pendingCount!=null && pendingCount>0){
                throw new BusinessException(400,"已有待生效的转让,禁止重复发起");
            }
            // 插入一条待生效的转让记录,1 天后由定时任务执行
            SuperAdminTransfer transfer=new SuperAdminTransfer();
            transfer.setFromUserId(StpUtil.getLoginIdAsLong());
            transfer.setToUserId(id);
            transfer.setStatus(0);
            transfer.setExpireTime(LocalDateTime.now().plusDays(1));
            superAdminTransferMapper.insert(transfer);
            return true;
        }
        // role=0/1:立即生效
        return update(new LambdaUpdateWrapper<User>().set(User::getRole,request.getRole()).eq(User::getId,id));
    }

    @Override
    public Boolean cancelTransfer(){
        SuperAdminTransfer transfer = superAdminTransferMapper.selectOne(
                new LambdaQueryWrapper<SuperAdminTransfer>()
                        .eq(SuperAdminTransfer::getFromUserId, StpUtil.getLoginIdAsLong())
                        .eq(SuperAdminTransfer::getStatus, 0)
        );
        if (transfer==null){
            throw new BusinessException(400,"没有待生效的转让");
        }
        transfer.setStatus(2);
        superAdminTransferMapper.updateById(transfer);
        return true;
    }

    @Override
    public Boolean sendResetCode(SendEmailCodeRequest sendEmailCodeRequest){
        String captchaKey=CacheKeys.CAPTCHA_PREFIX+sendEmailCodeRequest.getCaptchaId();
        String captchaCode=stringRedisTemplate.opsForValue().get(captchaKey);
        stringRedisTemplate.delete(captchaKey);
        if (captchaCode==null||!captchaCode.equalsIgnoreCase(sendEmailCodeRequest.getCaptchaCode())) {
            throw new BusinessException(400, "验证码错误或过期");
        }
        String email=sendEmailCodeRequest.getEmail();
        if(!lambdaQuery().eq(User::getEmail,email).exists()){
            return true;
        }
        doSendEmailCode(email,CacheKeys.EMAIL_CODE_PREFIX,CacheKeys.EMAIL_CD_PREFIX,CacheKeys.EMAIL_FAIL_PREFIX);
        return true;
    }

    @Override
    public Boolean sendBindingCode(SendEmailCodeRequest sendEmailCodeRequest){
        String captchaKey=CacheKeys.CAPTCHA_PREFIX+sendEmailCodeRequest.getCaptchaId();
        String captchaCode=stringRedisTemplate.opsForValue().get(captchaKey);
        stringRedisTemplate.delete(captchaKey);
        if (captchaCode==null||!captchaCode.equalsIgnoreCase(sendEmailCodeRequest.getCaptchaCode())) {
            throw new BusinessException(400, "验证码错误或过期");
        }
        String email=sendEmailCodeRequest.getEmail();
        if(lambdaQuery().eq(User::getEmail,email).exists()){
            throw new BusinessException(400,"邮箱已被占用");
        }
        doSendEmailCode(email,CacheKeys.EMAIL_BINDING_CODE_PREFIX,CacheKeys.EMAIL_BINDING_CD_PREFIX,CacheKeys.EMAIL_BINDING_FAIL_PREFIX);
        return true;
    }

    @Override
    public Boolean resetPasswordByEmail(EmailResetPasswordRequest emailResetPasswordRequest) {
        String email=emailResetPasswordRequest.getEmail();
        doCheckEmailCode(emailResetPasswordRequest.getEmailCode(),email,CacheKeys.EMAIL_CODE_PREFIX,CacheKeys.EMAIL_FAIL_PREFIX);
        boolean success=lambdaUpdate().eq(User::getEmail,email).set(User::getPassword,passwordEncoder.encode(emailResetPasswordRequest.getNewPassword())).update();
        log.info("绑定邮箱为{}的用户重置密码{}",email,success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean bindingEmailByEmail(EmailBindingRequest emailBindingRequest){
        String email= emailBindingRequest.getEmail();
        if (lambdaQuery().eq(User::getEmail,email).exists()){
            throw new BusinessException(400,"邮箱已被占用");
        }
        doCheckEmailCode(emailBindingRequest.getEmailCode(),email,CacheKeys.EMAIL_BINDING_CODE_PREFIX,CacheKeys.EMAIL_BINDING_FAIL_PREFIX);
        User user=getOptById(StpUtil.getLoginIdAsLong()).orElseThrow(()->new BusinessException(404,"意外错误，进行绑定邮箱操作的用户不存在"));
        boolean success=lambdaUpdate().eq(User::getId,StpUtil.getLoginIdAsLong()).set(User::getEmail,email).update();
        log.info("用户[{}]{}绑定邮箱{}{}",StpUtil.getLoginIdAsLong(),user.getUsername(),email,success?"成功":"失败");
        return success;
    }

    private void doSendEmailCode(String email,String codePrefix,String cdPrefix,String failPrefix){
        String cdEmailKey=cdPrefix+email;
        Boolean isFirstSend=stringRedisTemplate.opsForValue().setIfAbsent(cdEmailKey,"1",60,TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(isFirstSend)){
            throw new BusinessException(429,"发送太频繁，请60秒后重试");
        }
        String emailCode=RandomUtil.randomNumbers(6);
        String failKey=failPrefix+email;
        stringRedisTemplate.delete(failKey);
        String emailCodeKey=codePrefix+email;
        stringRedisTemplate.opsForValue().set(emailCodeKey,emailCode,5,TimeUnit.MINUTES);
        mailService.send(email,"图书管理系统验证码","您的验证码为："+emailCode);
        log.info("向邮箱{}发送邮件成功",email);
    }

    private void doCheckEmailCode(String emailCode,String email,String codePrefix,String failPrefix){
        String failKey=failPrefix+email;
        String failStr=stringRedisTemplate.opsForValue().get(failKey);
        String codeKey=codePrefix+email;
        int failCount=failStr==null?0:Integer.parseInt(failStr);
        if (failCount>=3){
            stringRedisTemplate.delete(codeKey);
            stringRedisTemplate.delete(failKey);
            throw new BusinessException(429,"验证码错误次数过多，请重新获取");
        }
        String code=stringRedisTemplate.opsForValue().get(codeKey);
        if (code==null||!code.equals(emailCode)){
            stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey,5,TimeUnit.MINUTES);
            throw new BusinessException(400,"邮箱验证码错误");
        }
        stringRedisTemplate.delete(codeKey);
        stringRedisTemplate.delete(failKey);
        log.info("邮箱{}检验验证码成功",email);
    }

    @Override
    public UserStatsResponse getUserStats(){
        long total=count();
        long user=lambdaQuery().eq(User::getRole,0).count();
        long admin=lambdaQuery().eq(User::getRole,1).count();
        long superAdmin=lambdaQuery().eq(User::getRole,2).count();
        List<StatItem> roleStatsList=new ArrayList<>(List.of(
                StatItem.builder().name("普通用户").value(user).build(),
                StatItem.builder().name("管理员").value(admin).build(),
                StatItem.builder().name("超级管理员").value(superAdmin).build()
        ));
        long able=lambdaQuery().eq(User::getStatus,1).count();
        long freeze=lambdaQuery().eq(User::getStatus,0).count();
        List<StatItem> statusStatsList=new ArrayList<>(List.of(
                StatItem.builder().name("启用").value(able).build(),
                StatItem.builder().name("禁用").value(freeze).build()
        ));
        List<Map<String,Object>> rows=userMapper.selectMaps(new QueryWrapper<User>().select("DATE_FORMAT(create_time,'%Y-%m-%d') AS date","COUNT(*) AS count").ge("create_time", LocalDate.now().minusDays(29).atStartOfDay()).groupBy("DATE_FORMAT(create_time,'%Y-%m-%d')"));
        Map<String,Long> dateCountMap=rows.stream().collect(Collectors.toMap(e->e.get("date").toString(),e->((Number)e.get("count")).longValue()));
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<TrendPoint> dayRegistrationTrendList=new ArrayList<>();
        for(int i=29;i>=0;i--){
            String date=LocalDateTime.now().minusDays(i).format(dateTimeFormatter);
            long count=dateCountMap.getOrDefault(date,0L);
            dayRegistrationTrendList.add(TrendPoint.builder().date(date).count(count).build());
        }
        return UserStatsResponse.builder().totalUsers(total).roleDistribution(roleStatsList).statusDistribution(statusStatsList).registrationTrend(dayRegistrationTrendList).build();
    }

    @Override
    public String uploadAvatar(MultipartFile file){
        if(file.isEmpty()){
            throw new BusinessException(400,"未接收到文件");
        }
        String type=file.getContentType();
        if((!"image/webp".equals(type))&&(!"image/jpeg".equals(type))&&(!"image/png".equals(type))){
            throw new BusinessException(400,"只支持 jpg/png/webp");
        }
        if(file.getSize()>4*1024*1024){
            throw new BusinessException(400,"图片不能超过 4MB");
        }
        String suffix=getSuffix(file);
        String filename= UUID.randomUUID()+suffix;
        File dir=new File("upload/avatar");
        if(!dir.exists()&&!dir.mkdirs()){
            throw new BusinessException(500,"创建头像存储目录失败");
        }
        try {
            file.transferTo(new File(dir, filename));
        } catch (IOException e) {
            throw new BusinessException(400,"文件上传失败");
        }
        String avatar="/avatar/"+filename;
        User user=getOptById(StpUtil.getLoginIdAsLong()).orElseThrow(()->new BusinessException(400,"意料之外的错误，进行操作的用户不存在"));
        boolean success=lambdaUpdate().eq(User::getId,StpUtil.getLoginIdAsLong()).set(User::getAvatar,avatar).update();
        log.info("用户{}上传头像照片{}",user.getUsername(),success?"成功":"失败");
        return avatar;
    }

    private String getSuffix(MultipartFile file){
        String originalName=file.getOriginalFilename();
        if(originalName!=null) {
            int dotId = originalName.lastIndexOf(".");
            if (dotId != -1) {
                return originalName.substring(dotId);
            }
        }
        String type=file.getContentType();
        if("image/jpeg".equals(type)){
            return ".jpg";
        }
        if("image/png".equals(type)){
            return ".png";
        }
        if("image/webp".equals(type)){
            return ".webp";
        }
        throw new BusinessException(400,"无法识别文件");
    }

    @Override
    public Boolean updateAvatar(UpdateAvatarRequest updateAvatarRequest){
        User user=getOptById(StpUtil.getLoginIdAsLong()).orElseThrow(()->new BusinessException(400,"意料之外的错误，进行操作的用户不存在"));
        boolean success=lambdaUpdate().eq(User::getId,StpUtil.getLoginIdAsLong()).set(User::getAvatar,updateAvatarRequest.getAvatar()).update();
        log.info("用户{}更新头像{}",user.getUsername(),success?"成功":"失败");
        return success;
    }
}
