package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体,对应数据库表 user
 */
@Data
@Builder
@TableName("user")
public class User {

    /** 用户ID,主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码(加密存储) */
    private String password;

    /** 角色:0=普通用户,1=管理员 */
    private Integer role;

    /** 状态:1=正常,0=禁用 */
    private Integer status;

    /**
     * 创建时间、更新时间由数据库默认值填充(DEFAULT CURRENT_TIMESTAMP),
     * 所以这里不手动赋值;插入/更新时 MyBatis-Plus 会忽略为 null 的字段。
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
