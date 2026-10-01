package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {
    /** 借书：用户借走某个副本 */
    void borrow(Long bookCopyId);

    /** 还书：归还某个副本 */
    void returnBook(Long bookCopyId);
    /** 查询：查询某个用户自己的借阅记录 */
    List<BorrowRecord> listMyRecord();
}

