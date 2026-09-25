package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.BorrowRequest;
import com.teamdev.bookmanagement.service.BorrowRecordService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowRecordService borrowRecordService;

    public BorrowController(BorrowRecordService borrowRecordService) {
        this.borrowRecordService = borrowRecordService;
    }

    /** 借书：POST /api/borrow，body 传 {"userId":1,"bookCopyId":2} */
    @PostMapping
    public Result<Void> borrow(@RequestBody BorrowRequest request) {
        borrowRecordService.borrow(request.getUserId(), request.getBookCopyId());
        return Result.success();
    }
}