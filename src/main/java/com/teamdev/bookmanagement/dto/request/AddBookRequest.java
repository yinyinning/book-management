package com.teamdev.bookmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddBookRequest {
    /** ISBN号 */
    @NotBlank
    @Size(max=20)
    @Pattern(regexp = "^\\S+$",message = "ISBN号不能包含空白字符")
    private String isbn;

    /** 书名 */
    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^\\S+$",message = "书名不能包含空白字符")
    private String title;

    /**作者*/
    @Size(max = 50)
    @Pattern(regexp = "^\\S+$",message = "作者名可以为空但不能包含空白字符")
    private String author;

    /** 出版社 */
    @Size(max = 100)
    @Pattern(regexp = "^\\S+$",message = "出版社可以为空但不能包含空白字符")
    private String publisher;
}
