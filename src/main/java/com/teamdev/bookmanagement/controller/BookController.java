package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    // 构造器注入,Spring 会自动把 BookService 传进来
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /** 按 id 查询单本书:GET /api/book/1 */
    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        return Result.success(bookService.getById(id));
    }

    /** 查询所有书:GET /api/book/list */
    @GetMapping("/list")
    public Result<List<Book>> list() {
        return Result.success(bookService.list());
    }

    /** 新增书:POST /api/book,body 传 JSON */
    @PostMapping
    public Result<Boolean> add(@RequestBody Book book) {
        return Result.success(bookService.save(book));
    }

    /** 修改书:PUT /api/book,body 传 JSON(必须带 id) */
    @PutMapping
    public Result<Boolean> update(@RequestBody Book book) {
        return Result.success(bookService.updateById(book));
    }

    /** 删除书:DELETE /api/book/{id} */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(bookService.removeById(id));
    }
}

