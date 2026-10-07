package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.AddBookRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookRequest;
import com.teamdev.bookmanagement.dto.response.BookResponse;
import com.teamdev.bookmanagement.dto.response.PageResponse;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.service.BookService;
import jakarta.validation.Valid;
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
    public Result<BookResponse> getById(@PathVariable Long id) {
        return Result.success(bookService.getBookById(id));
    }

    /** 查询所有书:GET /api/book/list */
    @GetMapping("/list")
    public Result<List<BookResponse>> list() {
        return Result.success(bookService.listBooks());
    }

    /** 模糊分页查书:GET /api/book/search */
    @GetMapping("/search")
    public Result<PageResponse<BookResponse>> search(@RequestParam String keyword,@RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "10") Integer pageSize){
        return Result.success(bookService.search(keyword,pageNum,pageSize));
    }

    /** 新增书:POST /api/book,body 传 JSON */
    @PostMapping
    @SaCheckRole("admin")
    public Result<Boolean> add(@Valid @RequestBody AddBookRequest addBookRequest) {
        return Result.success(bookService.addBook(addBookRequest));
    }

    /** 修改书:PUT /api/book,body 传 JSON(必须带 id) */
    @PutMapping
    @SaCheckRole("admin")
    public Result<Boolean> update(@Valid @RequestBody UpdateBookRequest updateBookRequest) {
        return Result.success(bookService.updateBook(updateBookRequest));
    }

    /** 删除书:DELETE /api/book/{id} */
    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(bookService.deleteBook(id));
    }
}

