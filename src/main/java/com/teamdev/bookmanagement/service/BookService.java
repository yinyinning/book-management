package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.AddBookRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookRequest;
import com.teamdev.bookmanagement.entity.Book;

public interface BookService extends IService<Book> {
    Boolean addBook(AddBookRequest addBookRequest);
    Boolean updateBook(UpdateBookRequest updateBookRequest);
    Boolean deleteBook(Long id);
}
