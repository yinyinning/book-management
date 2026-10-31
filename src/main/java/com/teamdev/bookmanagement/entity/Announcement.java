package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {
    /** 通告ID,主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 内容 */
    private String content;

    /** 类型 */
    private Integer type;

    /** 发布人ID */
    private Long publisherId;

    /** 发布时间 */
    private LocalDateTime publishTime;
    /** 发布人用户名(不落库) */
    @TableField(exist = false)
    private String publisherName;
}

