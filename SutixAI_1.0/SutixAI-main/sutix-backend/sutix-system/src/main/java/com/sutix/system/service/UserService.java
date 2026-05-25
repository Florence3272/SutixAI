package com.sutix.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.system.dto.LoginDTO;
import com.sutix.system.dto.RegisterDTO;
import com.sutix.system.dto.ResetPwdDTO;
import com.sutix.system.dto.UserInfoDTO;
import com.sutix.system.entity.User;

public interface UserService extends IService<User> {
    // 注册
    void register(RegisterDTO registerDTO);
    // 登录（返回JWT Token）
    String login(LoginDTO loginDTO);
    // 修改个人信息
    void updateUserInfo(Long userId, UserInfoDTO userInfoDTO);
    // 重置密码
    void resetPassword(Long userId, ResetPwdDTO resetPwdDTO);
    // 根据用户名查询用户
    User getUserByUsername(String username);
    // 校验是否为管理员
    boolean isAdmin(Long userId);
}