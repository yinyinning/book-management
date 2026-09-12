package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.LoginRequest;
import com.teamdev.bookmanagement.dto.LoginResponse;
import com.teamdev.bookmanagement.dto.RegisterRequest;
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

    /** 修改用户:PUT /api/user,body 传 JSON(必须带 id) */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }

    /** 删除用户:DELETE /api/user/{id} */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody RegisterRequest registerRequest){
        userService.register(registerRequest);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return Result.success(userService.login(loginRequest));
    }
}
