package com.teamdev.bookmanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.mapper.BookMapper;
import com.teamdev.bookmanagement.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
}
