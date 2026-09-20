package com.teamdev.bookmanagement.config;

import cn.dev33.satoken.stp.StpInterface;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    private final UserMapper userMapper;
    @Override
    public List<String> getRoleList(Object loginId,String loginType){
        Long userId=Long.valueOf(loginId.toString());
        User user=userMapper.selectById(userId);
        if (user==null){
            return List.of();
        }
        if (user.getRole()!=null&&user.getRole()==1){
            return List.of("admin");
        }
        return List.of("user");
    }
    @Override
    public List<String> getPermissionList(Object loginId,String loginType){
        return List.of();
    }
}
