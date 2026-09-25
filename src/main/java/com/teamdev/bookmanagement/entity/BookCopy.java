package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("book_copy")
public class BookCopy {
    /** 副本ID,主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 图书id */
    private Long bookId;

    /** 馆藏编码 */
    private String barcode;

    /**副本状态*/
    private Integer status;


}


