package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.BorrowRequest;
import com.teamdev.bookmanagement.dto.request.ReturnRequest;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import com.teamdev.bookmanagement.service.BorrowRecordService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowRecordService borrowRecordService;

    public BorrowController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    /** 借书：POST /api/borrow，body 传 {"bookCopyId":2} */
    @PostMapping
    public Result<Void> borrow(@Valid @RequestBody BorrowRequest request) {
        borrowRecordService.borrow(request.getBookCopyId());
        return Result.success();
    }

    /** 还书：PUT /api/borrow/return，body 传 {"bookCopyId":2}*/
    @PutMapping("/return")
    public Result<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        borrowRecordService.userReturnBook(request.getBookCopyId());
        return Result.success();
    }

    /** 管理员帮助还书：PUT /api/borrow/help/return，body 传 {"bookCopyId":2}*/
    @PutMapping("/help/return")
    public Result<Void> helpReturnBook(@Valid @RequestBody ReturnRequest request) {
        borrowRecordService.helpReturnBook(request.getBookCopyId());
        return Result.success();
    }
    /** 查询我的借阅记录：GET /api/borrow/my 管理员查询所有借阅记录，普通用户查询自己的借阅记录*/
    @GetMapping("/my")
    public Result<List<BorrowRecord>> listMyRecord() {
        return Result.success(borrowRecordService.listMyRecord());
    }

    /** 管理员查询某普通用户借阅记录：GET /api/borrow/{id} */
    @SaCheckRole("admin")
    @GetMapping("{id}")
    public Result<List<BorrowRecord>> listUserAllRecord(@PathVariable Long id) {
        return Result.success(borrowRecordService.listUserRecord(id));
    }
}