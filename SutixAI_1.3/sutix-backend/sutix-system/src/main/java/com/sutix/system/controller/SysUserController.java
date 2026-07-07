package com.sutix.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sutix.common.result.Result;
import com.sutix.system.dto.LoginDTO;
import com.sutix.system.dto.RegisterDTO;
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

    // 1. 登录功能（原有，已优化）
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {

        // 校验参数
        if (dto.getUsername() == null || dto.getPassword() == null) {
            return Result.fail("用户名或密码不能为空");
        }

        System.out.println("==========LOGIN==========");
        System.out.println("username=" + dto.getUsername());

        SysUser user = userService.getByUsername(dto.getUsername());

        System.out.println("user=" + user);

        if (user == null) {
            System.out.println("USER_NOT_FOUND");
            return Result.fail("USER_NOT_FOUND");
        }

        System.out.println("dbPassword=" + user.getPassword());

        boolean match =
                encoder.matches(
                        dto.getPassword(),
                        user.getPassword());

        System.out.println("passwordMatch=" + match);

        if (!match) {
            System.out.println("PASSWORD_ERROR");
            return Result.fail("PASSWORD_ERROR");
        }
        if (user.getStatus() == 0) return Result.fail("ACCOUNT_DISABLED");

        System.out.println("登录成功");

        String token = jwtUtil.createToken(user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        // 隐藏密码返回
        user.setPassword(null);
        map.put("user", user);
        return Result.success(map);
    }

    // 2. 检验用户名是否存在
    @GetMapping("/checkusername")
    public Result<Boolean> checkUsername(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        boolean exists = userService.existsByUsername(username);
        return Result.success(exists);
    }

    // 3. 注册功能
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO dto) {
        // 1. 参数校验
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            return Result.fail("用户名不能为空");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            return Result.fail("密码长度不能少于6位");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return Result.fail("两次密码输入不一致");
        }

        // 2. 校验用户名是否已存在
        if (userService.existsByUsername(dto.getUsername())) {
            return Result.fail("用户名已存在");
        }

        // 3. 构建用户对象并加密密码
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(dto.getPassword())); // 密码加密
        user.setNickname(dto.getNickname() == null ? dto.getUsername() : dto.getNickname()); // 昵称默认等于用户名
        user.setPhone(dto.getPhone());
        user.setStatus(1); // 账号默认启用（1=启用，0=禁用）
        user.setRoleId(2L); // 默认分配普通用户角色（sys_role表中id=2为普通用户）

        // 4. 保存用户
        boolean saveSuccess = userService.save(user);
        if (!saveSuccess) {
            return Result.fail("注册失败，请重试");
        }
        return Result.success("注册成功");
    }

    // 4. 获取当前用户登录信息（原有，已优化）
    @GetMapping("/info")
    public Result<SysUser> info() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return Result.fail("用户未登录");
        }
        SysUser user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户信息不存在");
        }
        // 隐藏敏感信息
        user.setPassword(null);
        return Result.success(user);
    }

}