package com.sutix.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sutix.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    SysUser selectByUsername(@Param("username") String username);
}