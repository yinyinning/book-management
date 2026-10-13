package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.AnnouncementRequest;
import com.teamdev.bookmanagement.entity.Announcement;
import com.teamdev.bookmanagement.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcement")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /** 发布公告/申诉：POST /api/announcement/add */
    @PostMapping("/add")
    public Result<Boolean> add(@Valid @RequestBody AnnouncementRequest request) {
        return Result.success(announcementService.add(request));
    }

    /** 查询通告列表：GET /api/announcement/list?type=0 */
    @GetMapping("/list")
    public Result<List<Announcement>> list(@RequestParam(required = false) Integer type) {
        return Result.success(announcementService.listByType(type));
    }

    /** 删除通告：DELETE /api/announcement/delete/{id} */
    @DeleteMapping("/delete/{id}")
    @SaCheckRole("admin")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(announcementService.deleteAnnouncement(id));
    }
}