package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.*;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
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

    /** 管理员按 id 查询单个用户:GET /api/user/1 */
    @GetMapping("/{id}")
    @SaCheckRole("admin")
    public Result<UserResponse> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    /** 管理员查询所有用户:GET /api/user/list */
    @GetMapping("/list")
    @SaCheckRole("admin")
    public Result<List<UserResponse>> list() {
        return Result.success(userService.listUsers());
    }

    /** 管理员禁用/启用用户:PUT /api/user/{id}/status */
    @PutMapping("/{id}/status")
    @SaCheckRole("admin")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @RequestBody @Valid UpdateUserStatusRequest updateUserStatusRequest){
        return Result.success(userService.updateUserStatus(id,updateUserStatusRequest));
    }

    /** 管理员删除用户:DELETE /api/user/{id} */
    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }

    /** 管理员重置密码:PUT /api/user/help/password*///会登出，重新登录
    @PutMapping("/help/password/{id}")
    @SaCheckRole("admin")
    public Result<Boolean> helpResetPassword(@PathVariable Long id){
        return Result.success(userService.helpResetPassword(id));
    }

    /** 修改用户名:PUT /api/user/name,body 传 JSON(必须带 id)(不须带id) 注意请求体传role/status有越权风险，需补充token鉴权处理*/
    //已完成
    @PutMapping("/name")
    public Result<Boolean> updateName(@RequestBody @Valid UpdateUserNameRequest updateUserNameRequest) {
        return Result.success(userService.updateUserName(updateUserNameRequest));
    }

    /** 修改密码:PUT /api/user/password*///会登出，需要重新登录
    @PutMapping("/password")
    public Result<Boolean> updatePassword(@RequestBody @Valid UpdateUserPasswordRequest updateUserPasswordRequest){
        Boolean result=userService.updateUserPassword(updateUserPasswordRequest);
        if (result){
            StpUtil.logout(StpUtil.getLoginIdAsLong());
        }
        return Result.success(result);
    }

    /** 注册普通用户:POST /api/user/register*/
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest registerRequest){
        userService.register(registerRequest);
        return Result.success();
    }

    /** 登录:POST /api/user/login*/
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return Result.success(userService.login(loginRequest));
    }

    /** 用户退出登录:POST /api/user/logout*/
    @PutMapping("/logout")
    public Result<Void> logout(){
        StpUtil.logout();
        return Result.success();
    }
    /** 管理员提拔:PUT /api/user/{id}/role */
    @PutMapping("/{id}/role")
    @SaCheckRole("super_admin")
    public Result<Boolean> updateUserRole(@PathVariable Long id, @RequestBody @Valid UpdateUserRoleRequest updateUserRoleRequest){
        return Result.success(userService.updateUserRole(id,updateUserRoleRequest));
    }
    /** 取消超级管理员转让:POST /api/user/transfer/cancel */
    @PostMapping("/transfer/cancel")
    @SaCheckRole("super_admin")
    public Result<Boolean> cancelTransfer(){
        return Result.success(userService.cancelTransfer());
    }
}
