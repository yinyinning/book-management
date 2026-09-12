package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.LoginRequest;
import com.teamdev.bookmanagement.dto.LoginResponse;
import com.teamdev.bookmanagement.dto.RegisterRequest;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.UserMapper;
import com.teamdev.bookmanagement.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
        if (user.getStatus()==0){
            throw new BusinessException(403,"账号已禁用");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BusinessException(400,"用户名或密码错误");
        }
        StpUtil.login(user.getId());
        return LoginResponse.builder().id(user.getId()).username(user.getUsername()).role(user.getRole()).token(StpUtil.getTokenValue()).build();
    }

    @Override
    public Boolean updateUser(User user) {
        if (user.getPassword()!=null&&!user.getPassword().isBlank()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return updateById(user);
    }
}
