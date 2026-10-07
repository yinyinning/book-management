package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.AddBookRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookRequest;
import com.teamdev.bookmanagement.dto.response.BookResponse;
import com.teamdev.bookmanagement.dto.response.PageResponse;
import com.teamdev.bookmanagement.entity.Book;

import java.util.List;

public interface BookService extends IService<Book> {
    BookResponse getBookById(Long bookId);
    List<BookResponse> listBooks();
    Boolean addBook(AddBookRequest addBookRequest);
    Boolean updateBook(UpdateBookRequest updateBookRequest);
    Boolean deleteBook(Long id);
    PageResponse<BookResponse> search(String keyword,Integer pageNum,Integer pageSize);
}
