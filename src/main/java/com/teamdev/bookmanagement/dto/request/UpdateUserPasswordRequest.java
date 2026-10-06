package com.teamdev.bookmanagement.dto.request;

import com.teamdev.bookmanagement.common.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordRequest {
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码只能6-20个字符")
    @Pattern(regexp = "^\\S+$",message = "密码不能包含空白字符")
    private String oldPassword;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码只能6-20个字符")
    @ValidPassword
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()]+$",message = "密码只能包含大写字母、小写字母、数字和!@#$%^&*()")
    private String newPassword;
}
