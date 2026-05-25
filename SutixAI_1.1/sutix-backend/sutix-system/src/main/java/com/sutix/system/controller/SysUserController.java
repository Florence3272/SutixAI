package com.sutix.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sutix.common.result.Result;
import com.sutix.system.dto.LoginDTO;
import com.sutix.system.entity.SysUser;
import com.sutix.system.service.SysUserService;
import com.sutix.common.utils.JwtUtil;
import com.sutix.system.utils.SecurityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class SysUserController {
    @Resource
    private SysUserService userService;
    @Resource
    private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // 登录
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        SysUser user = userService.getByUsername(dto.getUsername());
        if (user == null) return Result.fail("用户不存在");
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            return Result.fail("密码错误");
        }
        if (user.getStatus() == 0) return Result.fail("账号已禁用");

        String token = jwtUtil.createToken(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);
        return Result.success(map);
    }

    // 获取当前用户信息
    @GetMapping("/info")
    public Result<SysUser> info() {
        return Result.success(userService.getById(SecurityUtils.getUserId()));
    }

}