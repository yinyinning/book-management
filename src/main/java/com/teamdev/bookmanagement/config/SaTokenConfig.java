package com.teamdev.bookmanagement.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfig implements WebMvcConfigurer {

    private final UserService userService;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new SaInterceptor(handle-> checkLoginAndStatus())).addPathPatterns("/**").excludePathPatterns("/api/user/login","/api/user/register");
    }
    private void checkLoginAndStatus(){
        StpUtil.checkLogin();
        User user=userService.lambdaQuery().eq(User::getId,StpUtil.getLoginIdAsLong()).one();
        if (user==null||user.getStatus()==0){
            throw new BusinessException(403,"账号已禁用");
        }
    }
}
