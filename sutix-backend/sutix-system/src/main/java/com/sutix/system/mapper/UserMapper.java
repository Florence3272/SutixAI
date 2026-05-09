package com.sutix.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sutix.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 无需新增方法，BaseMapper已包含CRUD，仅需一个根据用户名查询的方法（可选）
    User selectByUsername(String username);
}