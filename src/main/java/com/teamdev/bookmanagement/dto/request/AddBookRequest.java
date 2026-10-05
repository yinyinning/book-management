package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBookRequest {
    /** ISBN号 */
    @NotBlank
    @Size(max=20)
    private String isbn;

    /** 书名 */
    @NotBlank
    @Size(max = 100)
    private String title;

    /**作者*/
    @Size(max = 50)
    private String author;

    /** 出版社 */
    @Size(max = 100)
    private String publisher;
}
