package com.teamdev.bookmanagement.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.teamdev.bookmanagement.entity.User;

/**
 * 用户 Mapper。
 * 继承 BaseMapper<User> 就自动获得了 selectById / insert / updateById / deleteById 等能力,
 * 无需写任何 SQL —— 这是 MyBatis-Plus 简化 CRUD 的核心。
 */
public interface UserMapper extends BaseMapper<User> {
}
