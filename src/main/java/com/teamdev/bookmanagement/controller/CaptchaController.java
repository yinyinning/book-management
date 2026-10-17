package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.response.CaptchaResponse;
import com.teamdev.bookmanagement.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/captcha")
public class CaptchaController {
    private final CaptchaService captchaService;
    @GetMapping
    public Result<CaptchaResponse> getCaptcha(){
        return Result.success(captchaService.getCaptcha());
    }
}
