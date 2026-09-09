package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户模块接口示例(增删改查)
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // 构造器注入,Spring 会自动把 UserService 传进来
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 按 id 查询单个用户:GET /api/user/1 */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /** 查询所有用户:GET /api/user/list */
    @GetMapping("/list")
    public Result<List<User>> list() {
        return Result.success(userService.list());
    }

    /** 新增用户:POST /api/user,body 传 JSON */
    @PostMapping
    public Result<Boolean> add(@RequestBody User user) {
        return Result.success(userService.save(user));
    }

    /** 修改用户:PUT /api/user,body 传 JSON(必须带 id) */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(userService.updateById(user));
    }

    /** 删除用户:DELETE /api/user/{id} */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}
