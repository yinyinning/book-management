package com.teamdev.bookmanagement.bootstrap;

import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.password}")
    private String adminPassword;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;
    @Override
    public void run(String... args){
        if (userService.lambdaQuery().eq(User::getRole,1).exists()){
            log.info("管理员已存在，跳过初始化");
            return;
        }
        if (adminPassword.isBlank()){
            log.warn("未设置管理员密码，不创建管理员");
            return;
        }
        if (userService.lambdaQuery().eq(User::getUsername,adminUsername).exists()){
            log.warn("存在用户与设置的管理员用户名重复，不创建管理员");
            return;
        }
        userService.save(User.builder().role(1).username(adminUsername).status(1).password(passwordEncoder.encode(adminPassword)).build());
    }
}
