package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "请输入合法用户名")
    @Size(min = 3,max = 20,message = "请输入合法用户名")
    private String username;
    @NotBlank(message = "请输入合法密码字符")
    @Size(min = 6,max = 20,message = "请输入合法密码字符")
    private String password;
}
