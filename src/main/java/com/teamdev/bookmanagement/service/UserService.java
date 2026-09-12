package com.teamdev.bookmanagement.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.teamdev.bookmanagement.dto.RegisterRequest;
import com.teamdev.bookmanagement.entity.User;

/**
 * 用户业务接口。
 * 继承 IService<User> 得到 save / list / getById / removeById 等更上层的方法(带批量、分页等)。
 */
public interface UserService extends IService<User> {
    void register(RegisterRequest registerRequest);
}
