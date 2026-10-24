package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.response.SystemStatsResponse;
import com.teamdev.bookmanagement.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system")
public class SystemController {
    private final SystemService systemService;

    @GetMapping("stats")
    @SaCheckRole("admin")
    public Result<SystemStatsResponse> getSystemStats(){
        return Result.success(systemService.getSystemStats());
    }
}
