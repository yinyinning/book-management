package com.teamdev.bookmanagement.dto;

import lombok.Data;

/** 借书请求参数 */
@Data
public class BorrowRequest {
    /** 用户ID */
    private Long userId;

    /** 副本ID */
    private Long bookCopyId;
}
