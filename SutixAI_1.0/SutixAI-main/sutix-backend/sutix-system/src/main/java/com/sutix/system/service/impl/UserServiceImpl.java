package com.sutix.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.common.utils.JwtUtil;
import com.sutix.system.dto.LoginDTO;
import com.sutix.system.dto.RegisterDTO;
import com.sutix.system.dto.ResetPwdDTO;
import com.sutix.system.dto.UserInfoDTO;
import com.sutix.system.entity.User;
import com.sutix.system.mapper.UserMapper;
import com.sutix.system.service.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 注册逻辑
    @Override
    public void register(RegisterDTO registerDTO) {
        // 1. 校验用户名是否已存在
        User existUser = getUserByUsername(registerDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 2. 加密密码
        String encryptPwd = BCrypt.hashpw(registerDTO.getPassword(), BCrypt.gensalt());
        // 3. 构建用户对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(encryptPwd);
        user.setNickname(registerDTO.getNickname());
        user.setRole(0); // 默认普通用户
        user.setStatus(0); // 默认正常
        // 4. 保存到数据库
        this.save(user);
    }

    // 登录逻辑（返回JWT Token）
    @Override
    public String login(LoginDTO loginDTO) {
        // 1. 查询用户
        User user = getUserByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        // 2. 校验密码
        if (!BCrypt.checkpw(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 3. 校验用户状态
        if (user.getStatus() == 1) {
            throw new RuntimeException("账号已禁用");
        }
        // 4. 生成JWT Token（调用你已有的JwtUtil）
        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }

    // 修改个人信息
    @Override
    public void updateUserInfo(Long userId, UserInfoDTO userInfoDTO) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 仅更新非空字段
        if (StringUtils.hasText(userInfoDTO.getNickname())) {
            user.setNickname(userInfoDTO.getNickname());
        }
        if (StringUtils.hasText(userInfoDTO.getPhone())) {
            user.setPhone(userInfoDTO.getPhone());
        }
        if (StringUtils.hasText(userInfoDTO.getEmail())) {
            user.setEmail(userInfoDTO.getEmail());
        }
        this.updateById(user);
    }

    // 重置密码
    @Override
    public void resetPassword(Long userId, ResetPwdDTO resetPwdDTO) {
        User user = this.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 校验旧密码
        if (!BCrypt.checkpw(resetPwdDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        // 加密新密码并更新
        String newEncryptPwd = BCrypt.hashpw(resetPwdDTO.getNewPassword(), BCrypt.gensalt());
        user.setPassword(newEncryptPwd);
        this.updateById(user);
    }

    // 根据用户名查询用户
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        return this.getOne(wrapper);
    }

    // 校验是否为管理员
    @Override
    public boolean isAdmin(Long userId) {
        User user = this.getById(userId);
        return user != null && user.getRole() == 1;
    }
}