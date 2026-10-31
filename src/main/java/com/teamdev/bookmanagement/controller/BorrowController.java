package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.BorrowRequest;
import com.teamdev.bookmanagement.dto.request.ReturnRequest;
import com.teamdev.bookmanagement.dto.response.BookBorrowStat;
import com.teamdev.bookmanagement.dto.response.BorrowRecordResponse;
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
    public Result<String> borrow(@Valid @RequestBody BorrowRequest request) {
        return Result.success(borrowRecordService.borrow(request.getBookCopyId()));
    }

    /** 还书：PUT /api/borrow/return，body 传 {"bookCopyId":2}*/
    @PutMapping("/return")
    public Result<Void> returnBook(@Valid @RequestBody ReturnRequest request) {
        borrowRecordService.userReturnBook(request.getBookCopyId());
        return Result.success();
    }

    /** 管理员帮助还书：PUT /api/borrow/help/return，body 传 {"bookCopyId":2}*/
    @SaCheckRole("admin")
    @PutMapping("/help/return")
    public Result<Void> helpReturnBook(@Valid @RequestBody ReturnRequest request) {
        borrowRecordService.helpReturnBook(request.getBookCopyId());
        return Result.success();
    }

    /** 查询我的借阅记录：GET /api/borrow/my 用户查询自己的借阅记录*/
    @GetMapping("/my")
    public Result<List<BorrowRecordResponse>> listMyRecord() {
        return Result.success(borrowRecordService.listMyRecord());
    }

    /** 查我借阅中的图书的ID列表：GET /api/borrow/my/bookIds */
    @GetMapping("/my/bookIds")
    public Result<List<Long>> getMyBookIds(){
        return Result.success(borrowRecordService.listBorrowBookIds());
    }

    /** 查我正借阅副本的数量：GET /api/borrow/my/borrowing/count */
    @GetMapping("/my/borrowing/count")
    public Result<Long> getMyBorrowingCount(){
        return Result.success(borrowRecordService.countBorrowing());
    }

    /** 管理员查询某普通用户借阅记录：GET /api/borrow/{id} */
    @SaCheckRole("admin")
    @GetMapping("/{id}")
    public Result<List<BorrowRecordResponse>> listUserAllRecord(@PathVariable Long id) {
        return Result.success(borrowRecordService.listUserRecord(id));
    }

    /** 管理员查询所有借阅记录：GET /api/borrow/all */
    @SaCheckRole("admin")
    @GetMapping("/all")
    public Result<List<BorrowRecordResponse>> listAllRecord(){
        return Result.success(borrowRecordService.listAllRecord());
    }
    /** 查询所有借阅次数 GET /api/borrow/stats*/
    @SaCheckRole("admin")
    @GetMapping("/stats")
    public Result<List<BookBorrowStat>> getBookBorrowStats(){
        return Result.success(borrowRecordService.getBookBorrowStats());
    }


    }