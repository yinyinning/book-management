package com.teamdev.bookmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String token;
    private Long id;
    private String username;
    private Integer role;
}
