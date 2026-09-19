package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserPasswordRequest {
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码只能6-20个字符")
    private String oldPassword;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码只能6-20个字符")
    private String newPassword;
}
