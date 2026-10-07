package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.request.AddBookRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookRequest;
import com.teamdev.bookmanagement.dto.response.BookResponse;
import com.teamdev.bookmanagement.dto.response.PageResponse;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.entity.BookType;
import com.teamdev.bookmanagement.entity.BookTypeRel;
import com.teamdev.bookmanagement.mapper.BookCopyMapper;
import com.teamdev.bookmanagement.mapper.BookMapper;
import com.teamdev.bookmanagement.mapper.BookTypeMapper;
import com.teamdev.bookmanagement.mapper.BookTypeRelMapper;
import com.teamdev.bookmanagement.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    private final BookCopyMapper bookCopyMapper;
    private final BookTypeRelMapper bookTypeRelMapper;
    private final BookTypeMapper bookTypeMapper;

    @Override
    public BookResponse getBookById(Long bookId){
        Book book=getOptById(bookId).orElseThrow(()->new BusinessException(404,"查找图书不存在"));
        BookResponse bookResponse=BookResponse.builder().isbn(book.getIsbn()).id(bookId).title(book.getTitle()).author(book.getAuthor()).publisher(book.getPublisher()).build();
        List<BookTypeRel> bookTypeRelList=new LambdaQueryChainWrapper<>(bookTypeRelMapper).eq(BookTypeRel::getBookId,bookId).list();
        if (bookTypeRelList.isEmpty()){
            bookResponse.setTypes(List.of());
            return bookResponse;
        }
        List<Long> typeIdList=bookTypeRelList.stream().map(BookTypeRel::getTypeId).toList();
        List<BookType> bookTypeList=new LambdaQueryChainWrapper<>(bookTypeMapper).in(BookType::getId,typeIdList).list();
        bookResponse.setTypes(bookTypeList);
        return bookResponse;
    }

    @Override
    public List<BookResponse> listBooks(){
        return toResponse(list());
    }

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

    @Override
    public PageResponse<BookResponse> search(String keyword,Integer pageNum,Integer pageSize){
        Page<Book> bookPage=lambdaQuery().and(m->m.like(Book::getTitle,keyword).or().like(Book::getAuthor,keyword).or().like(Book::getIsbn,keyword).or().like(Book::getPublisher,keyword)).page(new Page<>(pageNum,pageSize));
        return PageResponse.of(bookPage.getTotal(),pageNum,pageSize,toResponse(bookPage.getRecords()));
    }

    private List<BookResponse> toResponse(List<Book> bookList){
        if (bookList.isEmpty()){
            return List.of();
        }
        List<Long> bookIdList=bookList.stream().map(Book::getId).toList();
        List<BookTypeRel> bookTypeRelList=new LambdaQueryChainWrapper<>(bookTypeRelMapper).in(BookTypeRel::getBookId,bookIdList).list();
        Map<Long,List<BookType>> bookTypesMap;
        if (!bookTypeRelList.isEmpty()) {
            Set<Long> typeIdSet = bookTypeRelList.stream().map(BookTypeRel::getTypeId).collect(Collectors.toSet());
            Map<Long, BookType> typeMap = new LambdaQueryChainWrapper<>(bookTypeMapper).in(BookType::getId, typeIdSet).list().stream().collect(Collectors.toMap(BookType::getId, t -> t));
            bookTypesMap = bookTypeRelList.stream().collect(Collectors.groupingBy(BookTypeRel::getBookId, Collectors.mapping(r -> typeMap.get(r.getTypeId()), Collectors.toList())));
        } else {
            bookTypesMap = Map.of();
        }
        return bookList.stream().map(b->BookResponse.builder().publisher(b.getPublisher()).author(b.getAuthor()).title(b.getTitle()).isbn(b.getIsbn()).id(b.getId()).types(bookTypesMap.getOrDefault(b.getId(),List.of())).build()).toList();
    }
}
