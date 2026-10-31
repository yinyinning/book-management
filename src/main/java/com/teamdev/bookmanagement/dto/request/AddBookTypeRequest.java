package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBookTypeRequest {

    @NotBlank
    @Size(min = 2,max = 20,message = "分类名称应当在2-20个字符之间")
    @Pattern(regexp = "^\\S+$",message = "分类名称不能包含空白字符")
    private String name;
}
