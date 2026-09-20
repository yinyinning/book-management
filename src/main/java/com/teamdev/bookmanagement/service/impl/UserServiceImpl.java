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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 用户业务实现。
 * ServiceImpl<Mapper, Entity> 是 MyBatis-Plus 提供的通用实现,和上面的 IService 配套。
 * 后面要加自定义业务逻辑(比如登录校验),就在这里补方法。
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final BCryptPasswordEncoder passwordEncoder;
    public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder){
        passwordEncoder=bCryptPasswordEncoder;
    }
    @Override
    public void register(RegisterRequest registerRequest) {
        if (lambdaQuery().eq(User::getUsername,registerRequest.getUsername()).exists()){
            throw new BusinessException(400,"用户名已存在");
        }
        User user=User.builder().username(registerRequest.getUsername()).password(passwordEncoder.encode(registerRequest.getPassword())).role(0).status(1).build();
        save(user);
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
        if (loginRequest.getPassword()==null||loginRequest.getPassword().isBlank()||!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BusinessException(400,"用户名或密码错误");
        }
        StpUtil.login(user.getId());
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
        return update(new LambdaUpdateWrapper<User>().set(User::getUsername,updateUserNameRequest.getUsername()).eq(User::getId,StpUtil.getLoginIdAsLong()));
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
        return updateById(newUser);
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
        return update(new LambdaUpdateWrapper<User>().set(User::getStatus,updateUserStatusRequest.getStatus()).eq(User::getId,id));
    }
}
