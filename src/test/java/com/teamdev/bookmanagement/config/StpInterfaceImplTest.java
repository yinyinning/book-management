package com.teamdev.bookmanagement.config;

import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StpInterfaceImplTest {
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private StpInterfaceImpl stpInterface;

    @Test
    void getRoleList_管理员角色_返回admin() {
        User user=User.builder().role(1).build();
        when(userMapper.selectById(1L)).thenReturn(user);
        List<String> roles=stpInterface.getRoleList(1L,null);
        assertEquals(List.of("admin"),roles);
    }

    @Test
    void getRoleList_普通用户_返回user() {
        User user=User.builder().role(0).build();
        when(userMapper.selectById(1L)).thenReturn(user);
        List<String> roles=stpInterface.getRoleList(1L,null);
        assertEquals(List.of("user"),roles);
    }

    @Test
    void getRoleList_用户不存在_返回空() {
        when(userMapper.selectById(1L)).thenReturn(null);
        List<String> roles=stpInterface.getRoleList(1L,null);
        assertEquals(List.of(),roles);
    }

    @Test
    void getRoleList_role为null_返回user() {
        User user=User.builder().build();
        when(userMapper.selectById(1L)).thenReturn(user);
        List<String> roles=stpInterface.getRoleList(1L,null);
        assertEquals(List.of("user"),roles);
    }
}