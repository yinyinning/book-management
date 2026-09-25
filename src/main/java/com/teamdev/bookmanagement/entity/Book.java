package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
/** 书,对应数据库表 book */
@Data
@TableName("book")
public class Book {
    /** 书ID,主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** ISBN号 */
    private String isbn;

    /** 书名 */
    private String title;

    /**作者*/
    private String author;

    /** 出版社 */
    private String publisher;

    /**创建时间*/
    private LocalDateTime createTime;

    /**更新时间*/
    private LocalDateTime updateTime;


}

