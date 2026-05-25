package com.sutix.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);
}