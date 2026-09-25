package com.teamdev.bookmanagement.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import com.teamdev.bookmanagement.mapper.BorrowRecordMapper;
import com.teamdev.bookmanagement.service.BookCopyService;
import com.teamdev.bookmanagement.service.BorrowRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    // 注入 BookCopyService，用来查/改副本（构造器注入，和 controller 里一样）
    private final BookCopyService bookCopyService;

    public BorrowRecordServiceImpl(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @Override
    @Transactional
    public void borrow(Long userId, Long bookCopyId) {
        // 1. 查副本，不存在就报错
        BookCopy copy = bookCopyService.getById(bookCopyId);
        if (copy == null) {
            throw new BusinessException(404, "副本不存在");
        }

        // 2. 检查状态：必须是在馆(0)，否则不可借
        if (copy.getStatus() != 0) {
            throw new BusinessException(400, "该副本当前不可借（已借出或损坏）");
        }

        // 3. 生成借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookCopyId(bookCopyId);
        record.setBorrowTime(LocalDateTime.now());
        record.setDueTime(LocalDateTime.now().plusDays(30));
        record.setStatus(0);
        this.save(record);

        // 4. 副本状态改成借出(1)
        copy.setStatus(1);
        bookCopyService.updateById(copy);
    }
}