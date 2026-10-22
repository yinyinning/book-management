package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendEmailCodeRequest {
    @NotBlank(message = "验证码标识不能为空")
    private String captchaId;
    @NotBlank(message = "验证码不能为空")
    private String captchaCode;
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
}
