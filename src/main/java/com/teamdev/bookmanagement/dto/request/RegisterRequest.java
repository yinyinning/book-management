package com.teamdev.bookmanagement.dto.request;

import com.teamdev.bookmanagement.common.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3,max = 20,message = "用户名只能3-20个字符")
    @Pattern(regexp = "^[A-Za-z0-9()（）·`~！!@#￥$%…^&*_—=+【】{}|：:;；“”\"<>《》，。,./？?‘’'\\[\\]\\u4e00-\\u9fa5\\\\-]",message = "用户名存在非法字符")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6,max = 20,message = "密码只能6-20个字符")
    @ValidPassword
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*()]+$",message = "密码只能包含字母、数字和!@#$%^&*")
    private String password;
}
