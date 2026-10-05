package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateBookCopyRequest {
    @NotNull
    private Long id;

    /** 图书id */
    private Long bookId;

    /** 馆藏编码 */
    @Size(max = 100)
    private String barcode;

    /**副本状态*/
    @Min(0)
    @Max(2)
    private Integer status;

}
