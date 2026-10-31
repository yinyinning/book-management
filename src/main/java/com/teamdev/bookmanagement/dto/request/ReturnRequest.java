package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReturnRequest {
    @NotNull(message = "副本ID不能为空")
    private Long bookCopyId;
}
