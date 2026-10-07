package com.teamdev.bookmanagement.service.impl;

import java.util.List;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.teamdev.bookmanagement.common.BusinessException;
import com.teamdev.bookmanagement.dto.response.BorrowRecordResponse;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookCopy;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.mapper.BorrowRecordMapper;
import com.teamdev.bookmanagement.service.BookCopyService;
import com.teamdev.bookmanagement.service.BookService;
import com.teamdev.bookmanagement.service.BorrowRecordService;
import com.teamdev.bookmanagement.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {

    // 注入 BookCopyService，用来查/改副本（构造器注入，和 controller 里一样）
    private final BookCopyService bookCopyService;
    private final UserService userService;
    private final BookService bookService;

    private static final String[] statusTextArray={"借出中","已归还","逾期"};

    public BorrowRecordServiceImpl(BookCopyService bookCopyService,UserService userService,BookService bookService) {
        this.bookCopyService = bookCopyService;
        this.userService = userService;
        this.bookService=bookService;
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
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long bookCopyId,BorrowRecord record) {
        // 1. 查该副本「还没还」的那条记录（book_copy_id = ? 且 status = 0）最后比对id防止越权，由上一步进行普通用户与管理员的分流

        // 2. 查不到 → 该副本没有借出中的记录，无法归还
        if (record == null) {
            throw new BusinessException(400, "该副本没有相应的借出中的记录，无法归还");
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
        User user=userService.getOptById(record.getUserId()).orElseThrow(()->new BusinessException(404,"还书的用户不存在"));
        log.info("用户{}还副本{}成功",user.getUsername(),copy.getBarcode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userReturnBook(Long bookCopyId){
        BorrowRecord borrowRecord=lambdaQuery().eq(BorrowRecord::getBookCopyId,bookCopyId).eq(BorrowRecord::getStatus,0).eq(BorrowRecord::getUserId,StpUtil.getLoginIdAsLong()).one();
        returnBook(bookCopyId,borrowRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void helpReturnBook(Long bookCopyId){
        returnBook(bookCopyId,lambdaQuery().eq(BorrowRecord::getBookCopyId,bookCopyId).eq(BorrowRecord::getStatus,0).one());
        log.info("管理员帮还副本成功");
    }

    @Override
    public List<BorrowRecordResponse> listUserRecord(Long userId){
        userService.getOptById(userId).orElseThrow(()->new BusinessException(404,"查询借阅记录的用户不存在"));
        List<BorrowRecord> borrowRecordList=lambdaQuery().eq(BorrowRecord::getUserId,userId).orderByDesc(BorrowRecord::getBorrowTime).list();
        return toResponse(borrowRecordList);
    }

    @Override
    public List<BorrowRecordResponse> listMyRecord(){
        Long id=StpUtil.getLoginIdAsLong();
        return listUserRecord(id);
    }

    @Override
    public List<BorrowRecordResponse> listAllRecord(){
        return toResponse(lambdaQuery().orderByDesc(BorrowRecord::getBorrowTime).list());
    }

    private List<BorrowRecordResponse> toResponse(List<BorrowRecord> borrowRecordList){
        if (borrowRecordList.isEmpty()){
            return List.of();
        }
        Set<Long> userIdSet=borrowRecordList.stream().map(BorrowRecord::getUserId).collect(Collectors.toSet());
        List<User> userList=userService.lambdaQuery().in(User::getId,userIdSet).list();
        Map<Long,String> userNameMap=userList.stream().collect(Collectors.toMap(User::getId,User::getUsername));
        Set<Long> bookCopyIdSet=borrowRecordList.stream().map(BorrowRecord::getBookCopyId).collect(Collectors.toSet());
        List<BookCopy> bookCopyList=bookCopyService.lambdaQuery().in(BookCopy::getId,bookCopyIdSet).list();
        Map<Long,BookCopy> bookCopyMap=bookCopyList.stream().collect(Collectors.toMap(BookCopy::getId,t->t));
        Set<Long> bookIdSet=bookCopyList.stream().map(BookCopy::getBookId).collect(Collectors.toSet());
        List<Book> bookList;
        if (bookIdSet.isEmpty()){
            bookList=List.of();
        }
        else {
            bookList = bookService.lambdaQuery().in(Book::getId, bookIdSet).list();
        }
        Map<Long,String> bookTitleMap=bookList.stream().collect(Collectors.toMap(Book::getId,Book::getTitle));
        return borrowRecordList.stream().map(b->{
            BookCopy bookCopy=bookCopyMap.get(b.getBookCopyId());
            String username=userNameMap.get(b.getUserId());
            return BorrowRecordResponse.builder().borrowTime(b.getBorrowTime()).dueTime(b.getDueTime()).returnTime(b.getReturnTime()).barcode(bookCopy==null?"副本已删除":bookCopy.getBarcode()).title(bookCopy==null?"副本已删除":bookTitleMap.get(bookCopy.getBookId())).statusText(statusTextArray[b.getStatus()]).username(username==null?"用户已删除":username).build();
        }).toList();
    }
}