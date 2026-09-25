package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 借书记录,对应数据库表 borrow_record */
@Data
@TableName("borrow_record")
public class BorrowRecord {
    /** 记录ID,主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 副本ID */
    private Long bookCopyId;

    /** 借书时间 */
    private LocalDateTime borrowTime;

    /** 应还时间 */
    private LocalDateTime dueTime;

    /** 实际归还时间 */
    private LocalDateTime returnTime;

    /** 0=借出中 1=已归还 2=逾期 */
    private Integer status;


}

