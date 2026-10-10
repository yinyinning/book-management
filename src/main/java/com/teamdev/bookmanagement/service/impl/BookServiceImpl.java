package com.teamdev.bookmanagement.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.teamdev.bookmanagement.common.CacheKeys.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    private final BookCopyMapper bookCopyMapper;
    private final BookTypeRelMapper bookTypeRelMapper;
    private final BookTypeMapper bookTypeMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final String EMPTY_MARKER="EMPTY";

    @Override
    public BookResponse getBookById(Long bookId){
        String key=BOOK_DETAIL_PREFIX+bookId;
        Optional<BookResponse> bookResponseOptional=readCache(key, new TypeReference<>(){});
        if (bookResponseOptional.isPresent()){
            return bookResponseOptional.get();
        }
        String lockKey="lock:"+key;
        boolean gotLock=false;
        for (int i=0;i<6;i++){
            gotLock=Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey,"1",10,TimeUnit.SECONDS));
            if (gotLock){
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Optional<BookResponse> optionalBookResponse=readCache(key, new TypeReference<>(){});
            if (optionalBookResponse.isPresent()){
                return optionalBookResponse.get();
            }
        }
        if (!gotLock){
            throw new BusinessException(400,"连接超时");
        }
        try {
            Optional<BookResponse> bookResponseOpt = readCache(key, new TypeReference<>() {
            });
            if (bookResponseOpt.isPresent()) {
                return bookResponseOpt.get();
            }
            Optional<Book> bookOptional = getOptById(bookId);
            if (bookOptional.isEmpty()) {
                stringRedisTemplate.opsForValue().set(key, EMPTY_MARKER, 60, TimeUnit.SECONDS);
                throw new BusinessException(404, "查找图书不存在");
            }
            Book book = bookOptional.get();
            BookResponse bookResponse = BookResponse.builder().isbn(book.getIsbn()).id(bookId).title(book.getTitle()).author(book.getAuthor()).publisher(book.getPublisher()).build();
            List<BookTypeRel> bookTypeRelList = new LambdaQueryChainWrapper<>(bookTypeRelMapper).eq(BookTypeRel::getBookId, bookId).list();
            if (bookTypeRelList.isEmpty()) {
                bookResponse.setTypes(List.of());
                stringRedisTemplate.opsForValue().set(key, toJson(bookResponse), 30, TimeUnit.MINUTES);
                return bookResponse;
            }
            List<Long> typeIdList = bookTypeRelList.stream().map(BookTypeRel::getTypeId).toList();
            List<BookType> bookTypeList = new LambdaQueryChainWrapper<>(bookTypeMapper).in(BookType::getId, typeIdList).list();
            bookResponse.setTypes(bookTypeList);
            stringRedisTemplate.opsForValue().set(key, toJson(bookResponse), 30, TimeUnit.MINUTES);
            return bookResponse;
        }finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    public List<BookResponse> listBooks(){
        Optional<List<BookResponse>> optionalBookResponseList=readCache(BOOK_LIST_KEY,new TypeReference<>(){});
        if (optionalBookResponseList.isPresent()){
            return optionalBookResponseList.get();
        }
        String lockKey="lock:list";
        boolean gotLock=false;
        for (int i=0;i<6;i++){
            gotLock=Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey,"1",10,TimeUnit.SECONDS));
            if (gotLock){
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException("连接超时",e);
            }
            Optional<List<BookResponse>> optionalBookResponses=readCache(BOOK_LIST_KEY,new TypeReference<>(){});
            if (optionalBookResponses.isPresent()){
                return optionalBookResponses.get();
            }
        }
        if (!gotLock){
            throw new BusinessException(400,"连接超时");
        }
        try {
            Optional<List<BookResponse>> optionalBookResponses = readCache(BOOK_LIST_KEY, new TypeReference<>() {
            });
            if (optionalBookResponses.isPresent()) {
                return optionalBookResponses.get();
            }
            List<BookResponse> bookResponseList = toResponse(list());
            stringRedisTemplate.opsForValue().set(BOOK_LIST_KEY, toJson(bookResponseList), 2, TimeUnit.MINUTES);
            return bookResponseList;
        }finally {
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Override
    public Boolean addBook(AddBookRequest addBookRequest){
        if (lambdaQuery().eq(Book::getIsbn,addBookRequest.getIsbn()).exists()){
            throw new BusinessException(400,"ISBN已存在");
        }
        boolean success=save(Book.builder().author(addBookRequest.getAuthor()).publisher(addBookRequest.getPublisher()).title(addBookRequest.getTitle()).isbn(addBookRequest.getIsbn()).build());
        log.info("管理员[{}]新增图书《{}》(ISBN:{}){}", StpUtil.getLoginIdAsLong(),addBookRequest.getTitle(),addBookRequest.getIsbn(),success?"成功":"失败");
        stringRedisTemplate.delete(BOOK_LIST_KEY);
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
        if (success){
            stringRedisTemplate.delete(BOOK_DETAIL_PREFIX+book.getId());
            stringRedisTemplate.delete(BOOK_LIST_KEY);
        }
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
        if (success){
            stringRedisTemplate.delete(BOOK_DETAIL_PREFIX+id);
            stringRedisTemplate.delete(BOOK_LIST_KEY);
        }
        log.info("管理员[{}]删除id为[{}]的书《{}》(ISBN:{}){}",StpUtil.getLoginIdAsLong(),id,book.getTitle(),book.getIsbn(),success?"成功":"失败");
        return success;
    }

    @Override
    public PageResponse<BookResponse> search(String keyword,Integer pageNum,Integer pageSize){
        String key=BOOK_SEARCH_PREFIX+keyword+":"+pageNum+":"+pageSize;
        Optional<PageResponse<BookResponse>> optionalBookResponsePageResponse=readCache(key,new TypeReference<>(){});
        if (optionalBookResponsePageResponse.isPresent()){
            return optionalBookResponsePageResponse.get();
        }
        String lockKey="lock:"+key;
        boolean gotLock=false;
        for (int i=0;i<6;i++){
            gotLock=Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(lockKey,"1",10,TimeUnit.SECONDS));
            if (gotLock){
                break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Optional<PageResponse<BookResponse>> bookResponsePageResponseOpt=readCache(key,new TypeReference<>(){});
            if (bookResponsePageResponseOpt.isPresent()){
                return bookResponsePageResponseOpt.get();
            }
        }
        if (!gotLock){
            throw new BusinessException(400,"连接超时");
        }
        try {
            Optional<PageResponse<BookResponse>> bookResponsePageResponseOpt = readCache(key, new TypeReference<>() {
            });
            if (bookResponsePageResponseOpt.isPresent()) {
                return bookResponsePageResponseOpt.get();
            }
            Page<Book> bookPage = lambdaQuery().and(m -> m.like(Book::getTitle, keyword).or().like(Book::getAuthor, keyword).or().like(Book::getIsbn, keyword).or().like(Book::getPublisher, keyword)).page(new Page<>(pageNum, pageSize));
            PageResponse<BookResponse> bookResponsePageResponse = PageResponse.of(bookPage.getTotal(), pageNum, pageSize, toResponse(bookPage.getRecords()));
            stringRedisTemplate.opsForValue().set(key, toJson(bookResponsePageResponse), 2, TimeUnit.MINUTES);
            return bookResponsePageResponse;
        }finally {
            stringRedisTemplate.delete(lockKey);
        }
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

    private String toJson(Object object){
        try{
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化失败",e);
        }
    }

    private <T> Optional<T> readCache(String key, TypeReference<T> typeReference){
        String json=stringRedisTemplate.opsForValue().get(key);
        if (json==null){
            return Optional.empty();
        }
        if (EMPTY_MARKER.equals(json)){
            throw new BusinessException(404,"查找图书不存在");
        }
        try {
            return Optional.of(objectMapper.readValue(json,typeReference));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
