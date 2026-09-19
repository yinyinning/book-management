package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.LoginRequest;
import com.teamdev.bookmanagement.dto.request.RegisterRequest;
import com.teamdev.bookmanagement.dto.request.UpdateUserNameRequest;
import com.teamdev.bookmanagement.dto.request.UpdateUserPasswordRequest;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
import com.teamdev.bookmanagement.entity.User;
import com.teamdev.bookmanagement.service.UserService;
import jakarta.validation.Valid;
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
    public Result<UserResponse> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /** 查询所有用户:GET /api/user/list */
    @GetMapping("/list")
    public Result<List<UserResponse>> list() {
        return Result.success(userService.listUsers());
    }

    /** 修改用户名:PUT /api/user/name,body 传 JSON(必须带 id)(不须带id) 注意请求体传role/status有越权风险，需补充token鉴权处理*/
    //已完成
    @PutMapping("/name")
    public Result<Boolean> updateName(@RequestBody @Valid UpdateUserNameRequest updateUserNameRequest) {
        return Result.success(userService.updateUserName(updateUserNameRequest));
    }

    /** 修改密码:PUT /api/user/password*/
    @PutMapping("password")
    public Result<Boolean> updatePassword(@RequestBody @Valid UpdateUserPasswordRequest updateUserPasswordRequest){
        return Result.success(userService.updateUserPassword(updateUserPasswordRequest));
    }
    /** 删除用户:DELETE /api/user/{id} */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest registerRequest){
        userService.register(registerRequest);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return Result.success(userService.login(loginRequest));
    }
}
