package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.AddBookTypeRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookTypeRequest;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookType;

import java.util.List;

public interface BookTypeService extends IService<BookType> {
    Boolean addBookType(AddBookTypeRequest addBookTypeRequest);
    Boolean updateBookType(UpdateBookTypeRequest updateBookTypeRequest);
    Boolean deleteBookType(Long id);
    Boolean addBookTypes(Long bookId, List<Long> typeIds);
    Boolean resetBookTypes(Long bookId, List<Long> typeIds);
    Boolean clearBookTypes(Long bookId);
    List<BookType> getBookTypes(Long bookId);
    List<Book> getTypeBooks(Long typeId);
}
