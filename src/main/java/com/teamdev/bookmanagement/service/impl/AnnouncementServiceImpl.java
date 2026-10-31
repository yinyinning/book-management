package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.request.AnnouncementRequest;
import com.teamdev.bookmanagement.entity.Announcement;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.AnnouncementMapper;
import com.teamdev.bookmanagement.mapper.UserMapper;
import com.teamdev.bookmanagement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement>
        implements AnnouncementService {
    private final UserMapper userMapper;
    @Override
    public Boolean add(AnnouncementRequest request) {
        // 角色校验：公告(0)只有管理员能发，申诉(1)只有普通用户能发
        boolean isAdmin = StpUtil.hasRole("admin");
        if (request.getType() == 0 && !isAdmin) {
            throw new BusinessException(400, "只有管理员能发布公告");
        }
        if (request.getType() == 1 && isAdmin) {
            throw new BusinessException(400, "管理员不能发申诉");
        }

        // 构造实体（Announcement 没有 @Builder，用 new + setter）
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setType(request.getType());
        announcement.setPublisherId(StpUtil.getLoginIdAsLong());
        announcement.setPublishTime(LocalDateTime.now());

        boolean success = save(announcement);
        log.info("用户[{}]发布{}成功", StpUtil.getLoginIdAsLong(), request.getType() == 0 ? "公告" : "申诉");
        return success;

    }

    @Override
    public List<Announcement> listByType(Integer type) {
        List<Announcement> list;
        if (type == null) {
            list = lambdaQuery()
                    .orderByDesc(Announcement::getPublishTime)
                    .list();
        } else if (type == 1 && !StpUtil.hasRole("admin")) {
            list = lambdaQuery()
                    .eq(Announcement::getType, type)
                    .eq(Announcement::getPublisherId, StpUtil.getLoginIdAsLong())
                    .orderByDesc(Announcement::getPublishTime)
                    .list();
        } else {
            list = lambdaQuery()
                    .eq(Announcement::getType, type)
                    .orderByDesc(Announcement::getPublishTime)
                    .list();
        }
        fillPublisherName(list);
        return list;
    }

    @Override
    public Boolean deleteAnnouncement(Long id) {
        getOptById(id).orElseThrow(() -> new BusinessException(404, "通告不存在"));
        boolean success = removeById(id);
        log.info("管理员[{}]删除通告[{}]{}", StpUtil.getLoginIdAsLong(), id, success ? "成功" : "失败");
        return success;
    }
    private void fillPublisherName(List<Announcement> list){
        if (list.isEmpty()) {return;}
        Set<Long> publisherIds = list.stream()
                .map(Announcement::getPublisherId)
                .collect(Collectors.toSet());
        Map<Long, String> nameMap = userMapper.selectBatchIds(publisherIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
        list.forEach(a -> a.setPublisherName(nameMap.get(a.getPublisherId())));
    }

}
