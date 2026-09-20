package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateUserStatusRequest {
    @NotNull
    @Min(0)
    @Max(1)
    private Integer status;
}
