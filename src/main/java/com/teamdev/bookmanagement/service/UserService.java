package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.*;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
import com.teamdev.bookmanagement.dto.response.UserStatsResponse;
import com.teamdev.bookmanagement.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户业务接口。
 * 继承 IService<User> 得到 save / list / getById / removeById 等更上层的方法(带批量、分页等)。
 */
public interface UserService extends IService<User> {
    void register(RegisterRequest registerRequest,int role);
    Boolean deleteById(Long id);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse emailLogin(EmailLoginRequest emailLoginRequest);
    Boolean helpResetPassword(Long id);
    Boolean updateUserName(UpdateUserNameRequest updateUserNameRequest);
    Boolean updateUserPassword(UpdateUserPasswordRequest updateUserPasswordRequest);
    UserResponse getById(Long id);
    List<UserResponse> listUsers();
    Boolean updateUserStatus(Long id, UpdateUserStatusRequest updateUserStatusRequest);
    Boolean updateUserRole(Long id, UpdateUserRoleRequest request);
    Boolean cancelTransfer();
    Boolean sendResetCode(SendEmailCodeRequest sendEmailCodeRequest);
    Boolean resetPasswordByEmail(EmailResetPasswordRequest emailResetPasswordRequest);
    Boolean sendBindingCode(SendEmailCodeRequest sendEmailCodeRequest);
    Boolean bindingEmailByEmail(EmailBindingRequest emailBindingRequest);
    UserStatsResponse getUserStats();
    String uploadAvatar(MultipartFile file);
    Boolean updateAvatar(UpdateAvatarRequest updateAvatarRequest);
}
