package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.entity.BookCopy;

public interface BookCopyService extends IService<BookCopy> {
    Boolean addBookCopy(BookCopy bookCopy);
    Boolean updateBookCopy(BookCopy bookCopy);
    Boolean deleteBookCopy(Long id);
}
