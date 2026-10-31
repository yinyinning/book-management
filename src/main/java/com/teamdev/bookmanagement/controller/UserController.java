package com.teamdev.bookmanagement.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.teamdev.bookmanagement.common.Result;
import com.teamdev.bookmanagement.dto.request.*;
import com.teamdev.bookmanagement.dto.response.LoginResponse;
import com.teamdev.bookmanagement.dto.response.UserResponse;
import com.teamdev.bookmanagement.dto.response.UserStatsResponse;
import com.teamdev.bookmanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /** 管理员禁用/启用用户:PUT /api/user/{id}/status 要求操作者role大于被操作者*/
    @PutMapping("/{id}/status")
    @SaCheckRole("admin")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @RequestBody @Valid UpdateUserStatusRequest updateUserStatusRequest){
        return Result.success(userService.updateUserStatus(id,updateUserStatusRequest));
    }

    /** 管理员删除用户:DELETE /api/user/{id} */
    @DeleteMapping("/{id}")
    @SaCheckRole("admin")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.deleteById(id));
    }

    /** 管理员重置密码:PUT /api/user/help/password/{id} *///会登出，重新登录
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

    /** 注册普通用户:POST /api/user/register */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid RegisterRequest registerRequest){
        userService.register(registerRequest,0);
        return Result.success();
    }

    /** 超级管理员注册普通管理员: POST /api/user/register/admin */
    @PostMapping("/register/admin")
    @SaCheckRole("super_admin")
    public Result<Void> registerAdmin(@RequestBody @Valid RegisterRequest registerRequest){
        userService.register(registerRequest,1);
        return Result.success();
    }

    /** 普通用户名登录: POST /api/user/login */
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest){
        return Result.success(userService.login(loginRequest));
    }

    /** 邮箱地址登录: POST /api/user/email/login */
    @PostMapping("/email/login")
    public Result<LoginResponse> emailLogin(@RequestBody @Valid EmailLoginRequest emailLoginRequest){
        return Result.success(userService.emailLogin(emailLoginRequest));
    }

    /** 用户退出登录: PUT /api/user/logout */
    @PutMapping("/logout")
    public Result<Void> logout(){
        StpUtil.logout();
        return Result.success();
    }

    /** 用户未登录状态重置密码操作的发送验证码请求: POST  /api/user/password/send/email-code */
    @PostMapping("/password/send/email-code")
    public Result<Boolean> sendResetCode(@RequestBody @Valid SendEmailCodeRequest sendEmailCodeRequest){
        return Result.success(userService.sendResetCode(sendEmailCodeRequest));
    }

    /** 用户注册时或登录后在个人中心时绑定邮箱操作的发送验证码请求: POST  /api/user/binding/send/email-code */
    @PostMapping("/binding/send/email-code")
    public Result<Boolean> sendBindingCode(@RequestBody @Valid SendEmailCodeRequest sendEmailCodeRequest){
        return Result.success(userService.sendBindingCode(sendEmailCodeRequest));
    }

    /** 用户未登录状态重置密码操作的验证码校验: POST /api/user/password/reset */
    @PostMapping("/password/reset")
    public Result<Boolean> resetPasswordByEmail(@RequestBody @Valid EmailResetPasswordRequest emailResetPasswordRequest){
        return Result.success(userService.resetPasswordByEmail(emailResetPasswordRequest));
    }

    /** 用户登录后在个人中心绑定邮箱的验证码检验: POST /api/user/binding/email */
    @PostMapping("/binding/email")
    public Result<Boolean> bindingEmailByEmail(@RequestBody @Valid EmailBindingRequest emailBindingRequest){
        return Result.success(userService.bindingEmailByEmail(emailBindingRequest));
    }

    /** 管理员提拔: PUT /api/user/{id}/role */
    @PutMapping("/{id}/role")
    @SaCheckRole("super_admin")
    public Result<Boolean> updateUserRole(@PathVariable Long id, @RequestBody @Valid UpdateUserRoleRequest updateUserRoleRequest){
        return Result.success(userService.updateUserRole(id,updateUserRoleRequest));
    }

    /** 取消超级管理员转让: POST /api/user/transfer/cancel */
    @PostMapping("/transfer/cancel")
    @SaCheckRole("super_admin")
    public Result<Boolean> cancelTransfer(){
        return Result.success(userService.cancelTransfer());
    }

    /** 获取用户统计数据（总用户数，角色分组，状态分组，往前一月注册趋势）: GET /api/user/stats */
    @GetMapping("/stats")
    @SaCheckRole("admin")
    public Result<UserStatsResponse> getUserStats(){
        return Result.success(userService.getUserStats());
    }

    /** 上传头像文件: POST /api/user/avatar/upload */
    @PostMapping("/avatar/upload")
    public Result<String> uploadAvatar(@RequestParam("file")MultipartFile file){
        return Result.success(userService.uploadAvatar(file));
    }

    /** 更新头像: PUT /api/user/avatar/update */
    @PutMapping("/avatar/update")
    public Result<Boolean> updateAvatar(@RequestBody @Valid UpdateAvatarRequest updateAvatarRequest){
        return Result.success(userService.updateAvatar(updateAvatarRequest));
    }
}
