package com.sutix.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.system.entity.SysUser;
import com.sutix.system.mapper.SysUserMapper;
import com.sutix.system.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public SysUser getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }
}