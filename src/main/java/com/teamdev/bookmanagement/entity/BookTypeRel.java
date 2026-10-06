package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("book_type_rel")
public class BookTypeRel {

    /** 图书id */
    private Long bookId;

    /** 类型id */
    private Long typeId;
}
