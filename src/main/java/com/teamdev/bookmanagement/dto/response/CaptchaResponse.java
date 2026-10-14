package com.teamdev.bookmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CaptchaResponse {
    private String captchaId;
    private String image;
}
