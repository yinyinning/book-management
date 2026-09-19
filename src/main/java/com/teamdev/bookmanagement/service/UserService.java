package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.LoginRequest;
import com.teamdev.bookmanagement.dto.request.RegisterRequest;
import com.teamdev.bookmanagement.dto.request.UpdateUserNameRequest;
import com.teamdev.bookmanagement.dto.request.UpdateUserPasswordRequest;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
import com.teamdev.bookmanagement.entity.User;
import java.util.List;

/**
 * 用户业务接口。
 * 继承 IService<User> 得到 save / list / getById / removeById 等更上层的方法(带批量、分页等)。
 */
public interface UserService extends IService<User> {
    void register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    Boolean updateUserName(UpdateUserNameRequest updateUserNameRequest);
    Boolean updateUserPassword(UpdateUserPasswordRequest updateUserPasswordRequest);
    UserResponse getById(Long id);
    List<UserResponse> listUsers();
}
