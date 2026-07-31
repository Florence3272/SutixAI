package com.sutix.system.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;      // 用户名
    private String password;      // 密码
    private String confirmPassword; // 确认密码
    private String nickname;      // 昵称（可选）
    private String phone;         // 手机号（可选）
}