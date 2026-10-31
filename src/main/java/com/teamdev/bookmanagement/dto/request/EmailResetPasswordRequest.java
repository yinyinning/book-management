package com.teamdev.bookmanagement.dto.request;

import com.teamdev.bookmanagement.common.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmailResetPasswordRequest {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$",message = "验证码应当是六位数字")
    private String emailCode;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码只能6-20个字符")
    @ValidPassword
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()]+$",message = "密码只能包含大写字母、小写字母、数字和!@#$%^&*()")
    private String newPassword;
}
