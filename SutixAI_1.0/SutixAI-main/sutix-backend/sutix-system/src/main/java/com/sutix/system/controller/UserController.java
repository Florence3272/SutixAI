package com.sutix.system.controller;

import com.sutix.common.result.Result;
import com.sutix.common.utils.JwtUtil;
import com.sutix.system.dto.LoginDTO;
import com.sutix.system.dto.RegisterDTO;
import com.sutix.system.dto.ResetPwdDTO;
import com.sutix.system.dto.UserInfoDTO;
import com.sutix.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户管理接口", description = "包含用户注册、登录、信息修改、密码重置、管理员权限测试等接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    // 1. 注册接口（无需登录）
    @Operation(summary = "用户注册", description = "用户注册接口，无需登录，入参需符合注册规则（如用户名唯一、密码强度等）")
    @PostMapping("/register")
    public Result<?> register(@Parameter(description = "注册请求参数", required = true)@Valid @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功");
    }

    // 2. 登录接口（无需登录）
    @Operation(summary = "用户登录", description = "用户登录接口，无需登录，验证通过后返回JWT令牌和用户名")
    @PostMapping("/login")
    public Result<?> login(@Parameter(description = "登录请求参数（用户名+密码）", required = true)@Valid @RequestBody LoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        // 返回Token和基础用户信息
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", loginDTO.getUsername());
        return Result.success(data);
    }

    // 3. 修改个人信息（需要登录）
    @Operation(summary = "修改个人信息", description = "用户修改个人信息接口，需要登录（请求头携带有效Token）")
    @Parameters({
            @Parameter(name = "token", description = "用户登录后的JWT令牌", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "userInfoDTO", description = "个人信息修改参数", required = true)
    })
    @PutMapping("/info")
    public Result<?> updateUserInfo(
            @Valid @RequestBody UserInfoDTO userInfoDTO,
            @RequestHeader("token") String token
    ) {
        Long userId = JwtUtil.getUserIdFromToken(token);
        userService.updateUserInfo(userId, userInfoDTO);
        return Result.success("信息修改成功");
    }

    // 4. 重置密码（需要登录）
    @Operation(summary = "重置密码", description = "用户重置密码接口，需要登录（请求头携带有效Token），需验证原密码")
    @Parameters({
            @Parameter(name = "token", description = "用户登录后的JWT令牌", required = true, in = ParameterIn.HEADER),
            @Parameter(name = "resetPwdDTO", description = "密码重置参数（原密码+新密码）", required = true)
    })
    @PutMapping("/password")
    public Result<?> resetPassword(
            @Valid @RequestBody ResetPwdDTO resetPwdDTO,
            @RequestHeader("token") String token
    ) {
        Long userId = JwtUtil.getUserIdFromToken(token);
        userService.resetPassword(userId, resetPwdDTO);
        return Result.success("密码重置成功");
    }

    // 5. 测试管理员权限接口（仅管理员可访问）
    @Operation(summary = "管理员权限测试", description = "仅管理员可访问的测试接口，需验证Token对应的用户为管理员角色")
    @Parameter(name = "token", description = "用户登录后的JWT令牌", required = true, in = ParameterIn.HEADER)
    @GetMapping("/admin/test")
    public Result<?> adminTest(@RequestHeader("token") String token) {
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (!userService.isAdmin(userId)) {
            return Result.error(403, "仅管理员可访问");
        }
        return Result.success("管理员权限验证通过");
    }
}