package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserNameRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 20,message = "用户名只能3-20个字符")
    private String username;
}
