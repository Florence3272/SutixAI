package com.sutix.system.utils;

import com.sutix.system.entity.SysUser;

public class SecurityUtils {
    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static void setUser(SysUser user) {
        LOCAL.set(user);
    }

    public static SysUser getUser() {
        return LOCAL.get();
    }

    public static Long getUserId() {
        return LOCAL.get().getId();
    }

    public static void remove() {
        LOCAL.remove();
    }
}