package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBookCopyRequest {
    /** 图书id */
    @NotNull
    private Long bookId;

    /** 馆藏编码 */
    @NotBlank
    @Size(max = 100)
    private String barcode;
}
