package com.teamdev.bookmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamdev.bookmanagement.entity.BorrowRecord;
import org.apache.ibatis.annotations.Select;
import com.teamdev.bookmanagement.dto.response.BookBorrowStat;
import java.util.List;
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
@Select("SELECT b.id AS bookId, b.title AS title, COUNT(br.id) AS borrowCount " +
        "FROM book b " +
        "LEFT JOIN book_copy bc ON bc.book_id = b.id " +
        "LEFT JOIN borrow_record br ON br.book_copy_id = bc.id " +
        "GROUP BY b.id, b.title " +
        "ORDER BY borrowCount DESC")
    public List<BookBorrowStat> selectBookBorrowStats();
}

