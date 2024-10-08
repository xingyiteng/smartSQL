package com.iteng.startup.interceptor;

import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.exception.BusinessException;
import com.iteng.startup.model.entity.User;
import com.iteng.startup.service.UserService;
import com.iteng.startup.utils.CacheUtils;
import com.iteng.startup.utils.SpringContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.iteng.startup.constant.UserConstant.LOGIN_USER_ID;

/**
 * 登录拦截器
 * @author iteng
 * @date 2024-01-03 19:12
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从session中获取userId
        Long userId = (Long) CacheUtils.sessionCacheGet(LOGIN_USER_ID);
        if (userId == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录！");
        }
        // 查询数据库是否存在userId
        UserService userService = (UserService)SpringContextUtils.getBean("userServiceImpl");
        User user = userService.getById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录！");
        }
        // 放行
        return true;
    }
}
