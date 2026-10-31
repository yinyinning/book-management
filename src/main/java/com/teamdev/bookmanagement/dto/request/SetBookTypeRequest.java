package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SetBookTypeRequest {
    /** 要设置的书的分类的ID列表；空列表 = 无分类 */
    @NotNull(message = "typeIds不能为空")
    List<@NotNull Long> typeIds;
}
