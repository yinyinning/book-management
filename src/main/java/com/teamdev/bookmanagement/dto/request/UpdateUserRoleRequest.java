package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotNull(message = "角色不能为空")
    @Min(0)
    @Max(1)
    private Integer role;
}
