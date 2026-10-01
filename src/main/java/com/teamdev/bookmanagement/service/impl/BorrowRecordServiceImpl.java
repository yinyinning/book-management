package com.teamdev.bookmanagement.service.impl;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.BorrowRecordMapper;
import com.teamdev.bookmanagement.service.BookCopyService;
import com.teamdev.bookmanagement.service.BorrowRecordService;
import com.teamdev.bookmanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    // 注入 BookCopyService，用来查/改副本（构造器注入，和 controller 里一样）
    private final BookCopyService bookCopyService;
    private final UserService userService;

    public BorrowRecordServiceImpl(BookCopyService bookCopyService,UserService userService) {
        this.bookCopyService = bookCopyService;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrow(Long bookCopyId) {
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
        Long userId= StpUtil.getLoginIdAsLong();
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
        User user=userService.getOptById(userId).orElseThrow(()->new BusinessException(404,"借书操作的用户不存在"));
        log.info("用户{}借副本{}成功",user.getUsername(),copy.getBarcode());
    }
    @Override
    @Transactional
    public void returnBook(Long bookCopyId) {
        // 1. 查该副本「还没还」的那条记录（book_copy_id = ? 且 status = 0）
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getBookCopyId, bookCopyId)
                .eq(BorrowRecord::getStatus, 0);
        BorrowRecord record = this.getOne(wrapper);

        // 2. 查不到 → 该副本没有借出中的记录，无法归还
        if (record == null) {
            throw new BusinessException(400, "该副本没有借出中的记录，无法归还");
        }

        // 3. 填归还信息：returnTime = 现在；逾期记 2，否则记 1
        LocalDateTime now = LocalDateTime.now();
        record.setReturnTime(now);
        record.setStatus(now.isAfter(record.getDueTime()) ? 2 : 1);
        this.updateById(record);

        // 4. 副本状态改回在馆(0)
        BookCopy copy = bookCopyService.getById(bookCopyId);
        copy.setStatus(0);
        bookCopyService.updateById(copy);
    }
    @Override
    public List<BorrowRecord> listMyRecord(){
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId);
        return this.list(wrapper);
    }
}