package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.SetBookTypeRequest;
import com.teamdev.bookmanagement.entity.BookType;
import com.teamdev.bookmanagement.service.BookTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookCategoryController {

    private final BookTypeService bookTypeService;

    /** GET /api/book/{bookId}/types 查询某本书的分类 */
    @GetMapping("/{bookId}/types")
    public Result<List<BookType>> getBookTypes(@PathVariable Long bookId){
        return Result.success(bookTypeService.getBookTypes(bookId));
    }

    /** PUT /api/book/add/{bookId}/types 额外设置某本书的分类，body 传 {"typeIds":[1,2]} */
    @PutMapping("/add/{bookId}/types")
    @SaCheckRole("admin")
    public Result<Boolean> addBookTypes(@PathVariable Long bookId, @Valid @RequestBody SetBookTypeRequest setBookTypeRequest){
        return Result.success(bookTypeService.addBookTypes(bookId,setBookTypeRequest.getTypeIds()));
    }

    /** PUT /api/book/reset/{bookId}/types 清空原分类，重新设分类，body 传 {"typeIds":[1,2]} */
    @PutMapping("/reset/{bookId}/types")
    @SaCheckRole("admin")
    public Result<Boolean> resetBookTypes(@PathVariable Long bookId,@Valid @RequestBody SetBookTypeRequest setBookTypeRequest){
        return Result.success(bookTypeService.resetBookTypes(bookId,setBookTypeRequest.getTypeIds()));
    }

    /** DELETE /api/book/clear/{bookId}/types 清空某本书的分类 */
    @DeleteMapping("/clear/{bookId}/types")
    @SaCheckRole("admin")
    public Result<Boolean> clearBookTypes(@PathVariable Long bookId){
        return Result.success(bookTypeService.clearBookTypes(bookId));
    }
}
