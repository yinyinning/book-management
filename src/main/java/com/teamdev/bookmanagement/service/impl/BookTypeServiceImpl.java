package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.request.AddBookTypeRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookTypeRequest;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookType;
import com.teamdev.bookmanagement.entity.BookTypeRel;
import com.teamdev.bookmanagement.mapper.BookTypeMapper;
import com.teamdev.bookmanagement.mapper.BookTypeRelMapper;
import com.teamdev.bookmanagement.service.BookService;
import com.teamdev.bookmanagement.service.BookTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookTypeServiceImpl extends ServiceImpl<BookTypeMapper, BookType> implements BookTypeService {
    private final BookTypeRelMapper bookTypeRelMapper;
    private final BookService bookService;

    @Override
    public Boolean addBookType(AddBookTypeRequest addBookTypeRequest){
        if (lambdaQuery().eq(BookType::getName,addBookTypeRequest.getName()).exists()){
            throw new BusinessException(400,"分类已存在");
        }
        boolean success=save(BookType.builder().name(addBookTypeRequest.getName()).build());
        log.info("管理员[{}]增加图书分类“{}”{}",StpUtil.getLoginIdAsLong(),addBookTypeRequest.getName(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean updateBookType(UpdateBookTypeRequest updateBookTypeRequest){
        BookType bookType=getOptById(updateBookTypeRequest.getId()).orElseThrow(()->new BusinessException(404,"分类不存在"));
        if (lambdaQuery().eq(BookType::getName,updateBookTypeRequest.getName()).ne(BookType::getId,updateBookTypeRequest.getId()).exists()){
            throw new BusinessException(400,"分类已存在");
        }
        boolean success=lambdaUpdate().set(BookType::getName,updateBookTypeRequest.getName()).eq(BookType::getId,updateBookTypeRequest.getId()).update();
        log.info("管理员[{}]更新id为[{}]的分类“{}”为“{}”{}",StpUtil.getLoginIdAsLong(),updateBookTypeRequest.getId(),bookType.getName(),updateBookTypeRequest.getName(),success?"成功":"失败");
        return success;
    }

    @Override
    public Boolean deleteBookType(Long id){
        BookType bookType=getOptById(id).orElseThrow(()->new BusinessException(404,"要删除的分类不存在"));
        if (new LambdaQueryChainWrapper<>(bookTypeRelMapper).eq(BookTypeRel::getTypeId,id).exists()){
            throw new BusinessException(400,"该分类下还有书，不能删除");
        }
        boolean success=removeById(id);
        log.info("管理员[{}]删除id为[{}]的分类“{}”{}",StpUtil.getLoginIdAsLong(),id,bookType.getName(),success?"成功":"失败");
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addBookTypes(Long bookId, List<Long> typeIdList){
        Book book=bookService.getOptById(bookId).orElseThrow(()->new BusinessException(404,"书不存在"));
        List<Long> typeIds=new ArrayList<>(new LinkedHashSet<>(typeIdList));
        for (Long typeId:typeIds){
            BookType bookType=getOptById(typeId).orElseThrow(()->new BusinessException(404,"分类不存在"));
            if (new LambdaQueryChainWrapper<>(bookTypeRelMapper).eq(BookTypeRel::getBookId,bookId).eq(BookTypeRel::getTypeId,typeId).exists()){
                log.info("图书[{}]《{}》(ISBN:{})已有分类[{}]“{}”",bookId,book.getTitle(),book.getIsbn(),typeId,bookType.getName());
                continue;
            }
            if (!(bookTypeRelMapper.insert(BookTypeRel.builder().bookId(bookId).typeId(typeId).build())>0)){
                throw new BusinessException(400,"设置图书分类失败");
            }
            log.info("管理员[{}]设置图书[{}]《{}》(ISBN:{})分类为[{}]“{}”成功",StpUtil.getLoginIdAsLong(),book.getId(),book.getTitle(),book.getIsbn(),bookType.getId(),bookType.getName());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean clearBookTypes(Long bookId){
        Book book=bookService.getOptById(bookId).orElseThrow(()->new BusinessException(404,"要清空分类的书不存在"));
        bookTypeRelMapper.delete(new LambdaQueryWrapper<BookTypeRel>().eq(BookTypeRel::getBookId,bookId));
        log.info("管理员[{}]清空图书[{}]《{}》(ISBN:{})的分类成功",StpUtil.getLoginIdAsLong(),bookId,book.getTitle(),book.getIsbn());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean resetBookTypes(Long bookId,List<Long> typeIdList){
        return clearBookTypes(bookId)&&addBookTypes(bookId,typeIdList);
    }

    @Override
    public List<BookType> getBookTypes(Long bookId){
        Book book=bookService.getOptById(bookId).orElseThrow(()->new BusinessException(404,"查询分类的书不存在"));
        List<BookTypeRel> bookTypeRelList=new LambdaQueryChainWrapper<>(bookTypeRelMapper).eq(BookTypeRel::getBookId,bookId).list();
        List<Long> bookTypeIdList=new ArrayList<>();
        for (BookTypeRel bookTypeRel:bookTypeRelList){
            bookTypeIdList.add(bookTypeRel.getTypeId());
        }
        if (bookTypeIdList.isEmpty()){
            return List.of();
        }
        List<BookType> bookTypeList=lambdaQuery().in(BookType::getId,bookTypeIdList).list();
        log.debug("查询书[{}]《{}》(ISBN:{})的分类成功",book.getId(),book.getTitle(),book.getIsbn());
        return bookTypeList;
    }

    @Override
    public List<Book> getTypeBooks(Long typeId){
        BookType bookType=getOptById(typeId).orElseThrow(()->new BusinessException(404,"查询其下书的分类不存在"));
        List<BookTypeRel> bookTypeRelList=new LambdaQueryChainWrapper<>(bookTypeRelMapper).eq(BookTypeRel::getTypeId,typeId).list();
        List<Long> bookIdList=new ArrayList<>();
        for (BookTypeRel bookTypeRel:bookTypeRelList){
            bookIdList.add(bookTypeRel.getBookId());
        }
        if(bookIdList.isEmpty()){
            return List.of();
        }
        List<Book> bookList=bookService.lambdaQuery().in(Book::getId,bookIdList).list();
        log.debug("查询分类[{}]“{}”下的图书成功",bookType.getId(),bookType.getName());
        return bookList;
    }
}
