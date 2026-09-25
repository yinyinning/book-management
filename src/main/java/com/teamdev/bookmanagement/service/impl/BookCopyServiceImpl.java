package com.teamdev.bookmanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.mapper.BookCopyMapper;
import com.teamdev.bookmanagement.service.BookCopyService;
import org.springframework.stereotype.Service;

@Service
public class BookCopyServiceImpl extends ServiceImpl<BookCopyMapper, BookCopy> implements BookCopyService {
}
