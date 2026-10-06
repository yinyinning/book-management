package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.AddBookTypeRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookRequest;
import com.teamdev.bookmanagement.dto.request.UpdateBookTypeRequest;
import com.teamdev.bookmanagement.entity.Book;
import com.teamdev.bookmanagement.entity.BookType;
import com.teamdev.bookmanagement.service.BookTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book-type")
public class BookTypeController {

    private final BookTypeService bookTypeService;

    /** GET /api/book-type/list 查全部分类 */
    @GetMapping("/list")
    public Result<List<BookType>> getAllBookType(){
        return Result.success(bookTypeService.list());
    }

    /** GET /api/book-type/{typeId}/books 查该分类下的所有书 */
    @GetMapping("/{typeId}/books")
    public Result<List<Book>> getTypeBooks(@PathVariable Long typeId){
        return Result.success(bookTypeService.getTypeBooks(typeId));
    }

    /** POST /api/book-type/add 新增分类，body 传 {"name":"科幻"} */
    @PostMapping("/add")
    @SaCheckRole("admin")
    public Result<Boolean> addBookType(@Valid @RequestBody AddBookTypeRequest addBookTypeRequest){
        return Result.success(bookTypeService.addBookType(addBookTypeRequest));
    }

    /** PUT /api/book-type/update 改分类名，body 传 {"id":"1","name":"畅销"} */
    @PutMapping("/update")
    @SaCheckRole("admin")
    public Result<Boolean> updateBookType(@Valid @RequestBody UpdateBookTypeRequest updateBookTypeRequest){
        return Result.success(bookTypeService.updateBookType(updateBookTypeRequest));
    }

    /** DELETE /api/book-type/delete/{id} 删分类 */
    @DeleteMapping("/delete/{id}")
    @SaCheckRole("admin")
    public Result<Boolean> deleteBookType(@PathVariable Long id){
        return Result.success(bookTypeService.deleteBookType(id));
    }
}
