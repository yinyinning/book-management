package com.teamdev.bookmanagement.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class BorrowRecordResponse {

    /** 记录ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 副本BARCODE */
    private String barcode;

    /** 书名 */
    private String title;

    /** 借书时间 */
    private LocalDateTime borrowTime;

    /** 应还时间 */
    private LocalDateTime dueTime;

    /** 实际归还时间 */
    private LocalDateTime returnTime;

    /** 0=借出中 1=已归还 2=逾期 */
    private String statusText;

}
