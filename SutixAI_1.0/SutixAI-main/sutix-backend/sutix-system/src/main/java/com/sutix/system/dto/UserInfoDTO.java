package com.sutix.system.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String nickname;        // 昵称（可选）
    private String phone;           // 手机号（可选）
    private String email;           // 邮箱（可选）
}