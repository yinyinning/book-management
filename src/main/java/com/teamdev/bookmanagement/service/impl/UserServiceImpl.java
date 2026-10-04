package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.request.*;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.UserMapper;
import com.teamdev.bookmanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户业务实现。
 * ServiceImpl<Mapper, Entity> 是 MyBatis-Plus 提供的通用实现,和上面的 IService 配套。
 * 后面要加自定义业务逻辑(比如登录校验),就在这里补方法。
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.user_password}")
    private String resetUserPassword;
    private final BCryptPasswordEncoder passwordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private static final int MAX_FAIL_COUNT=5;
    private static final int LOCK_MINUTES=5;
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, StringRedisTemplate stringRedisTemplate){
        passwordEncoder=bCryptPasswordEncoder;
        this.stringRedisTemplate=stringRedisTemplate;
    }
    @Override
    public void register(RegisterRequest registerRequest) {
        if (adminUsername.equals(registerRequest.getUsername())||lambdaQuery().eq(User::getUsername,registerRequest.getUsername()).exists()){
            throw new BusinessException(400,"用户名已存在");
        }
        User user=User.builder().username(registerRequest.getUsername()).password(passwordEncoder.encode(registerRequest.getPassword())).role(0).status(1).build();
        save(user);
        log.info("用户{}注册成功",user.getUsername());
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
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
        return LoginResponse.builder().id(user.getId()).username(user.getUsername()).role(user.getRole()).token(StpUtil.getTokenValue()).build();
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
        return UserResponse.builder().id(user.getId()).username(user.getUsername()).status(user.getStatus()).role(user.getRole()).createTime(user.getCreateTime()).updateTime(user.getUpdateTime()).build();
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
        boolean success=update(new LambdaUpdateWrapper<User>().set(User::getStatus,updateUserStatusRequest.getStatus()).eq(User::getId,id));
        if (success){
            if (updateUserStatusRequest.getStatus()==0) {
                StpUtil.kickout(id);
            }
            log.info("管理员{}将用户{}账号{}成功",adminUser.getUsername(),user.getUsername(),updateUserStatusRequest.getStatus()==0?"冻结":"启用");
        }
        return success;
    }
}
