package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.AnnouncementRequest;
import com.teamdev.bookmanagement.entity.Announcement;

import java.util.List;

public interface AnnouncementService extends IService<Announcement> {
    Boolean add(AnnouncementRequest announcementRequest);
    List<Announcement> listByType(Integer type);
    Boolean deleteAnnouncement(Long announcementId);
}
