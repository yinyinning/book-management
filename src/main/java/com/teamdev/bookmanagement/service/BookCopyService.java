package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.request.AddBookCopyRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookCopyRequest;
import com.teamdev.bookmanagement.entity.BookCopy;

public interface BookCopyService extends IService<BookCopy> {
    Boolean addBookCopy(AddBookCopyRequest addBookCopyRequest);
    Boolean updateBookCopy(UpdateBookCopyRequest updateBookCopyRequest);
    Boolean deleteBookCopy(Long id);
}
