package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
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
    public Boolean addBook(Book book){
        if (book.getTitle()==null||book.getTitle().isBlank()||book.getIsbn()==null||book.getIsbn().isBlank()){
            throw new BusinessException(400,"书名和isbn不能为空");
        }
        if (lambdaQuery().eq(Book::getIsbn,book.getIsbn()).exists()){
            throw new BusinessException(400,"ISBN已存在");
        }
        boolean success=save(book);
        log.info("管理员[{}]新增图书《{}》(ISBN:{}){}", StpUtil.getLoginIdAsLong(),book.getTitle(),book.getIsbn(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean updateBook(Book updateBook){
        if (updateBook.getId()==null){
            throw new BusinessException(400,"更改书本的id不能为空");
        }
        Book book=getOptById(updateBook.getId()).orElseThrow(()->new BusinessException(404,"书不存在"));
        if (!(updateBook.getIsbn()==null||updateBook.getIsbn().isBlank())&&lambdaQuery().eq(Book::getIsbn,updateBook.getIsbn()).ne(Book::getId,updateBook.getId()).exists()){
            throw new BusinessException(400,"isbn已被占用，更新失败");
        }
        boolean success=updateById(updateBook);
        log.info("管理员[{}]更新id为[{}]的书《{}》(ISBN:{})为书《{}》(ISBN:{}){}",StpUtil.getLoginIdAsLong(),updateBook.getId(),book.getTitle(),book.getIsbn(),updateBook.getTitle(),updateBook.getIsbn(),success?"成功":"失败");
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
