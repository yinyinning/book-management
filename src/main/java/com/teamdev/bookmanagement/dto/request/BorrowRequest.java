package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 借书请求参数 */
@Data
public class BorrowRequest {
    /** 副本ID */
    @NotNull(message = "副本ID不能为空")
    private Long bookCopyId;
}
