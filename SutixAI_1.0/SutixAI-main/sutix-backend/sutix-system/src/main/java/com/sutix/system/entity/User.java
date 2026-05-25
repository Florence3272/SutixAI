package com.sutix.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;                // 用户ID
    private String username;        // 用户名
    private String password;        // 加密密码
    private String nickname;        // 昵称
    private String phone;           // 手机号
    private String email;           // 邮箱
    private Integer role;           // 角色：0=普通，1=管理员
    private Integer status;         // 状态：0=正常，1=禁用
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime; // 创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime; // 更新时间
    @TableLogic
    private Integer deleted;        // 逻辑删除
}