package com.sutix.system.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ResetPwdDTO {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;     // 旧密码（明文）
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "新密码长度6-20位")
    private String newPassword;     // 新密码（明文）
}