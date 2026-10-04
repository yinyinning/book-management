package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.entity.Book;

public interface BookService extends IService<Book> {
    Boolean addBook(Book book);
    Boolean updateBook(Book book);
    Boolean deleteBook(Long id);
}
