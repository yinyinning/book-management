package com.teamdev.bookmanagement.dto.request;

import lombok.Data;

/** 借书请求参数 */
@Data
public class BorrowRequest {
    /** 副本ID */
    private Long bookCopyId;
}
