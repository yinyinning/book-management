package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("book_type")
public class BookType {

    /** 类型id，主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型名称 */
    private String name;
}
