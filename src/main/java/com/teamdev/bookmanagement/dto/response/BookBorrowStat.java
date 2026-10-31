package com.teamdev.bookmanagement.dto.response;


import lombok.Data;

@Data
public class BookBorrowStat {
    /**书ID*/
    private Long bookId;
    /**书名*/
    private String title;
    /**借阅次数*/
    private Long borrowCount;


}
