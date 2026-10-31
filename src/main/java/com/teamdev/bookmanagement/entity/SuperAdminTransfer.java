package com.teamdev.bookmanagement.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("super_admin_transfer")
public class SuperAdminTransfer {
    /** 转让记录ID,主键自增 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发起转让的超级管理员ID */
    private Long fromUserId;

    /** 被提拔者ID */
    private Long toUserId;

    /** 生效时间 */
    private LocalDateTime expireTime;

    /** 发起时间 */
    private LocalDateTime createTime;

    /** 0=待生效 1=已生效 2=已取消 */
    private Integer status;


}