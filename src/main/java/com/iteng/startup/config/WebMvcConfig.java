package com.iteng.startup.config;

import com.iteng.startup.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 全局跨域配置
 * @author iteng
 * @date 2023-12-29 18:47
 */
@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {

    /**
     * 请求白名单
     */
    private static final List<String> pathWhite = Arrays.asList("/spark-ai/**", "/database/**", "/user/login","/user/captcha","/user/register", "/user/email_captcha", "/user/email_register", "/user/forgot_password");

    /**
     * swagger放行白名单
     */
    private static final List<String> swaggerWhite = Arrays.asList("/swagger-resources","/v2/api-docs","/favicon.ico");
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 允许发送 Cookie
                .allowCredentials(true)
                // 前端域名
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                // 放行所有域名 .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    // 开发可以不设置登录拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(pathWhite)
                .excludePathPatterns(swaggerWhite)
                // 放行静态文件
                .excludePathPatterns( "/**/*.html", "/**/*.js", "/**/*.css", "/**/*.woff", "/**/*.ttf");
    }
}
