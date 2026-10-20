package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailLoginRequest {
    @NotBlank(message = "验证码标识不能为空")
    private String captchaId;
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
    @NotBlank(message = "请输入合法邮箱地址")
    @Email(message = "请输入合法邮箱地址")
    private String email;
    @NotBlank(message = "请输入合法密码字符")
    @Size(min = 6,max = 20,message = "请输入合法密码字符")
    private String password;
}
