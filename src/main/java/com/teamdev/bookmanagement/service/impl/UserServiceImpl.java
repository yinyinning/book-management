package com.teamdev.bookmanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
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
}
