package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.request.AddBookCopyRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookCopyRequest;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import com.teamdev.bookmanagement.mapper.BookCopyMapper;
import com.teamdev.bookmanagement.mapper.BorrowRecordMapper;
import com.teamdev.bookmanagement.service.BookCopyService;
import com.teamdev.bookmanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookCopyServiceImpl extends ServiceImpl<BookCopyMapper, BookCopy> implements BookCopyService {
    private final BookService bookService;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    public Boolean addBookCopy(AddBookCopyRequest addBookCopyRequest){
        Book book=bookService.getOptById(addBookCopyRequest.getBookId()).orElseThrow(()->new BusinessException(404,"图书不存在"));
        if (lambdaQuery().eq(BookCopy::getBarcode,addBookCopyRequest.getBarcode()).exists()){
            throw new BusinessException(400,"馆藏编码已存在");
        }
        boolean success=save(BookCopy.builder().bookId(addBookCopyRequest.getBookId()).barcode(addBookCopyRequest.getBarcode()).status(0).build());
        log.info("管理员[{}]增加图书《{}》(ISBN:{})的副本(BARCODE:{}){}", StpUtil.getLoginIdAsLong(),book.getTitle(),book.getIsbn(),addBookCopyRequest.getBarcode(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean updateBookCopy(UpdateBookCopyRequest updateBookCopyRequest){
        BookCopy bookCopy=getOptById(updateBookCopyRequest.getId()).orElseThrow(()->new BusinessException(404,"副本不存在"));
        Book book=bookService.getOptById(bookCopy.getBookId()).orElseThrow(()->new BusinessException(404,"意料外的错误:原副本图书不存在"));
        if (!(updateBookCopyRequest.getBarcode()==null||updateBookCopyRequest.getBarcode().isBlank())&&lambdaQuery().eq(BookCopy::getBarcode,updateBookCopyRequest.getBarcode()).ne(BookCopy::getId,updateBookCopyRequest.getId()).exists()){
            throw new BusinessException(400,"barcode已被占用，更新失败");
        }
        if (updateBookCopyRequest.getBarcode()==null||updateBookCopyRequest.getBarcode().isBlank()){
            updateBookCopyRequest.setBarcode(bookCopy.getBarcode());
        }
        if (updateBookCopyRequest.getBookId()==null){
            updateBookCopyRequest.setBookId(bookCopy.getBookId());
        }
        if (updateBookCopyRequest.getStatus()==null){
            updateBookCopyRequest.setStatus(bookCopy.getStatus());
        }
        Book updateBook=bookService.getOptById(updateBookCopyRequest.getBookId()).orElseThrow(()->new BusinessException(404,"图书不存在"));
        boolean success=updateById(BookCopy.builder().id(updateBookCopyRequest.getId()).barcode(updateBookCopyRequest.getBarcode()).bookId(updateBookCopyRequest.getBookId()).status(updateBookCopyRequest.getStatus()).build());
        log.info("管理员[{}]更新副本id为[{}]的书《{}》(ISBN:{})的副本(BARCODE:{})更新为书《{}》(ISBN:{})的副本(BARCODE:{}){}",StpUtil.getLoginIdAsLong(),updateBookCopyRequest.getId(),book.getTitle(),book.getIsbn(),bookCopy.getBarcode(),updateBook.getTitle(),updateBook.getIsbn(),updateBookCopyRequest.getBarcode(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean deleteBookCopy(Long id){
        BookCopy bookCopy=getOptById(id).orElseThrow(()->new BusinessException(404,"副本不存在"));
        Book book=bookService.getOptById(bookCopy.getBookId()).orElseThrow(()->new BusinessException(404,"意料外的错误:删除的副本图书不存在"));
        if (new LambdaQueryChainWrapper<>(borrowRecordMapper).eq(BorrowRecord::getBookCopyId,id).eq(BorrowRecord::getStatus,0).exists()){
            throw new BusinessException(400,"该副本有未归还的借阅记录,无法删除");
        }
        boolean success=removeById(id);
        log.info("管理员[{}]删除副本id为[{}]的书《{}》(ISBN:{})的副本(BARCODE:{}){}",StpUtil.getLoginIdAsLong(),id,book.getTitle(),book.getIsbn(),bookCopy.getBarcode(),success?"成功":"失败");
        return success;
    }
}
