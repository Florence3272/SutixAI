package com.sutix.system.utils;

import com.sutix.system.entity.SysUser;

/**
 * 当前请求的用户上下文（线程安全）
 * AuthInterceptor 在每个请求的 preHandle 中设置，afterCompletion 中清除。
 */
public class SecurityUtils {
    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static void setUser(SysUser user) {
        LOCAL.set(user);
    }

    public static SysUser getUser() {
        return LOCAL.get();
    }

    public static Long getUserId() {
        SysUser user = LOCAL.get();
        return user == null ? null : user.getId();
    }

    /**
     * 判断当前用户是否为管理员
     */
    public static boolean isAdmin() {
        SysUser user = LOCAL.get();
        if (user == null) return false;
        return user.getRoleId() != null && user.getRoleId() == 1L;
    }

    /**
     * 判断当前用户是否为普通用户
     */
    public static boolean isUser() {
        SysUser user = LOCAL.get();
        if (user == null) return false;
        return user.getRoleId() != null && user.getRoleId() == 2L;
    }

    public static void remove() {
        LOCAL.remove();
    }
}
