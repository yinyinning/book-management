package com.teamdev.bookmanagement.dto.response;

import com.teamdev.bookmanagement.entity.BookType;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BookResponse {

    /** 书ID */
    private Long id;

    /** ISBN号 */
    private String isbn;

    /** 书名 */
    private String title;

    /**作者*/
    private String author;

    /** 出版社 */
    private String publisher;

    /** 图书分类集合 */
    private List<BookType> types;
}
