package com.sutix.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sutix.system.entity.SysRole;
import com.sutix.system.entity.SysUser;
import com.sutix.system.mapper.SysUserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    public UserDetailsServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("id", userId);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) throw new UsernameNotFoundException("用户不存在：" + userId);

        // 根据roleId单独查角色
        SysRole role = sysUserMapper.selectById(user.getRoleId()).getRole();
        String roleKey = role == null ? "" : role.getRoleKey();

        List<GrantedAuthority> auths = new ArrayList<>();
        if(StringUtils.hasText(roleKey)){
            auths.add(new SimpleGrantedAuthority("ROLE_"+roleKey));
        }

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(auths)
                .disabled(user.getStatus()==0)
                .build();
    }
}
