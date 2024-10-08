package com.iteng.startup.aop;

import com.iteng.startup.annotation.AuthCheck;
import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.constant.UserConstant;
import com.iteng.startup.exception.BusinessException;
import com.iteng.startup.model.entity.User;
import com.iteng.startup.service.UserService;
import com.iteng.startup.utils.CacheUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 鉴权切面
 *
 * @author iteng
 * @date 2024-01-05 20:17
 */
@Aspect
@Component
public class AuthCheckAop {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doAuthCheck(ProceedingJoinPoint pjp, AuthCheck authCheck) throws Throwable {
        // 获取登录的用户信息
        User user = userService.getLoginUser();

        // 获取登录用户权限
        Integer userRole = user.getUserRole();

        // 将anyRole数组转为set
        Set<Integer> anyRole = Arrays.stream(authCheck.anyRole()).distinct().boxed().collect(Collectors.toSet());

        // 拥有任意权限即通过
        if (!CollectionUtils.isEmpty(anyRole)) {
            if (!anyRole.contains(userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 必须有指定权限才通过
        int mustRole = authCheck.mustRole();
        if (mustRole != -1) {
            if (!(mustRole == userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 通过权限校验，放行
        return pjp.proceed();
    }
}
