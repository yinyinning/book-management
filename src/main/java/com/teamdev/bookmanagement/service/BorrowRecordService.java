package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.entity.BorrowRecord;

public interface BorrowRecordService extends IService<BorrowRecord> {
    /** 借书：用户借走某个副本 */
    void borrow(Long userId, Long bookCopyId);
}

