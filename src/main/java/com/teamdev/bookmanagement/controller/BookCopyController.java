package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.service.BookCopyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-copy")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    // 构造器注入,Spring 会自动把 BookCopyService 传进来
    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    /** 按 id 查询单副本:GET /api/book-copy/1 */
    @GetMapping("/{id}")
    public Result<BookCopy> getById(@PathVariable Long id) {
        return Result.success(bookCopyService.getById(id));
    }

    /** 查询所有副本:GET /api/book-copy/list */
    @GetMapping("/list")
    public Result<List<BookCopy>> list() {
        return Result.success(bookCopyService.list());
    }

    /** 新增副本:POST /api/book-copy,body 传 JSON */
    @PostMapping
    public Result<Boolean> add(@RequestBody BookCopy bookCopy) {
        return Result.success(bookCopyService.save(bookCopy));
    }

    /** 修改副本:PUT /api/book-copy,body 传 JSON(必须带 id) */
    @PutMapping
    public Result<Boolean> update(@RequestBody BookCopy bookCopy) {
        return Result.success(bookCopyService.updateById(bookCopy));
    }

    /** 删除副本:DELETE /api/book-copy/{id} */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(bookCopyService.removeById(id));
    }
}
