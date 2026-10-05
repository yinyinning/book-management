package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateBookRequest {
    @NotNull
    private Long id;

    /** ISBN号 */
    @Size(max = 20)
    private String isbn;

    /** 书名 */
    @Size(max = 100)
    private String title;

    /**作者*/
    @Size(max = 50)
    private String author;

    /** 出版社 */
    @Size(max = 100)
    private String publisher;

}
