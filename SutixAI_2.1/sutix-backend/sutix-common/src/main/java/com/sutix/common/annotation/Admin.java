package com.sutix.common.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

/**
 * 标记接口仅允许管理员（admin 角色）访问。
 * <p>
 * 使用方式：在 Controller 方法上添加 @Admin 即可，
 * 等同于 @PreAuthorize("hasRole('admin')")
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasRole('admin')")
public @interface Admin {
}
