package com.teamdev.bookmanagement.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.teamdev.bookmanagement.entity.SuperAdminTransfer;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.SuperAdminTransferMapper;
import com.teamdev.bookmanagement.mapper.UserMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SuperAdminTransferScheduler {

    private final SuperAdminTransferMapper superAdminTransferMapper;
    private final UserMapper userMapper;

    public SuperAdminTransferScheduler(SuperAdminTransferMapper superAdminTransferMapper, UserMapper userMapper){
        this.superAdminTransferMapper = superAdminTransferMapper;
        this.userMapper = userMapper;
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void executeDueTransfers(){
        List<SuperAdminTransfer> dueList = superAdminTransferMapper.selectList(
                new LambdaQueryWrapper<SuperAdminTransfer>()
                        .eq(SuperAdminTransfer::getStatus, 0)
                        .le(SuperAdminTransfer::getExpireTime, LocalDateTime.now())
        );
        for (SuperAdminTransfer t : dueList){
            // 被提拔的人 -> 超级管理员(2)
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .set(User::getRole, 2)
                    .eq(User::getId, t.getToUserId()));
            // 发起的人 -> 普通用户(0)
            userMapper.update(null, new LambdaUpdateWrapper<User>()
                    .set(User::getRole, 0)
                    .eq(User::getId, t.getFromUserId()));
            // 转让记录 -> 已生效(1)
            t.setStatus(1);
            superAdminTransferMapper.updateById(t);
        }
    }
}
