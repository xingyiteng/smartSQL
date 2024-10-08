package com.iteng.startup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 * @author iteng
 * @date 2023-12-29 18:38
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    /**
     * 有任何一个角色
     */
    int[] anyRole() default {};

    /**
     * 必须有某个角色
     */
    int mustRole() default -1;
}
