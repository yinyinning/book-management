package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.request.AddBookRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookRequest;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.mapper.BookCopyMapper;
import com.teamdev.bookmanagement.mapper.BookMapper;
import com.teamdev.bookmanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    private final BookCopyMapper bookCopyMapper;

    @Override
    public Boolean addBook(AddBookRequest addBookRequest){
        if (lambdaQuery().eq(Book::getIsbn,addBookRequest.getIsbn()).exists()){
            throw new BusinessException(400,"ISBN已存在");
        }
        boolean success=save(Book.builder().author(addBookRequest.getAuthor()).publisher(addBookRequest.getPublisher()).title(addBookRequest.getTitle()).isbn(addBookRequest.getIsbn()).build());
        log.info("管理员[{}]新增图书《{}》(ISBN:{}){}", StpUtil.getLoginIdAsLong(),addBookRequest.getTitle(),addBookRequest.getIsbn(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean updateBook(UpdateBookRequest updateBookRequest){
        Book book=getOptById(updateBookRequest.getId()).orElseThrow(()->new BusinessException(404,"书不存在"));
        if (!(updateBookRequest.getIsbn()==null||updateBookRequest.getIsbn().isBlank())&&lambdaQuery().eq(Book::getIsbn,updateBookRequest.getIsbn()).ne(Book::getId,updateBookRequest.getId()).exists()){
            throw new BusinessException(400,"isbn已被占用，更新失败");
        }
        if (updateBookRequest.getIsbn()==null||updateBookRequest.getIsbn().isBlank()){
            updateBookRequest.setIsbn(book.getIsbn());
        }
        if (updateBookRequest.getTitle()==null||updateBookRequest.getTitle().isBlank()){
            updateBookRequest.setTitle(book.getTitle());
        }
        if (updateBookRequest.getAuthor()==null||updateBookRequest.getAuthor().isBlank()){
            updateBookRequest.setAuthor(book.getAuthor());
        }
        if (updateBookRequest.getPublisher()==null||updateBookRequest.getPublisher().isBlank()){
            updateBookRequest.setPublisher(book.getPublisher());
        }
        boolean success=updateById(Book.builder().id(updateBookRequest.getId()).isbn(updateBookRequest.getIsbn()).title(updateBookRequest.getTitle()).publisher(updateBookRequest.getPublisher()).author(updateBookRequest.getAuthor()).build());
        log.info("管理员[{}]更新id为[{}]的书《{}》(ISBN:{})为书《{}》(ISBN:{}){}",StpUtil.getLoginIdAsLong(),updateBookRequest.getId(),book.getTitle(),book.getIsbn(),updateBookRequest.getTitle(),updateBookRequest.getIsbn(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean deleteBook(Long id){
        Book book=getOptById(id).orElseThrow(()->new BusinessException(404,"书不存在"));
        if (new LambdaQueryChainWrapper<>(bookCopyMapper).eq(BookCopy::getBookId,id).exists()){
            throw new BusinessException(400,"该书下还有副本，无法删除");
        }
        boolean success=removeById(id);
        log.info("管理员[{}]删除id为[{}]的书《{}》(ISBN:{}){}",StpUtil.getLoginIdAsLong(),id,book.getTitle(),book.getIsbn(),success?"成功":"失败");
        return success;
    }
}
