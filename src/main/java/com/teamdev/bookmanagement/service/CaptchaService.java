package com.teamdev.bookmanagement.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import com.teamdev.bookmanagement.common.CacheKeys;
import com.teamdev.bookmanagement.dto.response.CaptchaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaService {
    private final StringRedisTemplate stringRedisTemplate;

    public CaptchaResponse getCaptcha() {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String code = captcha.getCode();
        String image = captcha.getImageBase64Data();
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(CacheKeys.CAPTCHA_PREFIX+uuid,code,5, TimeUnit.MINUTES);
        return CaptchaResponse.builder().captchaId(uuid).image(image).build();
    }
}
