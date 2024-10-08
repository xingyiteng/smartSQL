package com.iteng.startup.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 * @author iteng
 * @date 2024-01-02 20:51
 */
public class CacheUtils {


    /**
     * 缓存到session, 指定过期时间
     * @param key 缓存键
     * @param value 缓存值
     * @param expire 过期时间 默认s
     * @param <T> 缓存值的类型
     */
    public static <T> void sessionCacheSet(String key, T value, int expire){
        if (StringUtils.isBlank(key)) {
            return;
        }
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
        session.setAttribute(key, value);
        session.setMaxInactiveInterval(expire);
    }

    /**
     * 缓存到session, 默认过期时间 - 30min
     * @param key 缓存键
     * @param value 缓存值
     * @param <T> 缓存值的类型
     */
    public static <T> void sessionCacheSet(String key, T value){
        if (StringUtils.isBlank(key)) {
            return;
        }
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
        session.setAttribute(key, value);
    }

    /**
     * 从session中获取value
     * @param key 缓存键
     * @return 缓存值
     */
    public static Object sessionCacheGet(String key){
        if (StringUtils.isBlank(key)) {
            return null;
        }
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest().getSession();
        return session.getAttribute(key);
    }

    /**
     * 清除session缓存
     * @param key 缓存键
     */
    public static void sessionCacheClear(String key){
        if (StringUtils.isBlank(key)) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        session.removeAttribute(key);
    }

    /**
     * 缓存到redis 设置过期时间
     * @param key 缓存键
     * @param value 缓存值
     * @param expire 过期时间
     * @param timeUnit 时间单位
     */
    @SuppressWarnings("unchecked")
    public static <T> void redisCacheSet(String key, T value, long expire, TimeUnit timeUnit){
        if (StringUtils.isBlank(key)) {
            return;
        }
        RedisTemplate<String, T> redisTemplate = (RedisTemplate<String, T>) SpringContextUtils.getBean("redisTemplate");
        ValueOperations<String, T> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(key, value, expire, timeUnit);
    }

    /**
     * 缓存到redis 永久有效
     * @param key 缓存键
     * @param value 缓存值
     */
    @SuppressWarnings("unchecked")
    public static <T> void redisCacheSet(String key, T value){
        if (StringUtils.isBlank(key)) {
            return;
        }
        RedisTemplate<String, T> redisTemplate = (RedisTemplate<String, T>) SpringContextUtils.getBean("redisTemplate");
        ValueOperations<String, T> opsForValue = redisTemplate.opsForValue();
        opsForValue.set(key, value);
    }

    /**
     * 获取redis缓存
     * @param key 缓存键
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public static Object redisCacheGet(String key){
        if (StringUtils.isBlank(key)) {
            return null;
        }
        RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>)SpringContextUtils.getBean("redisTemplate");
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        return opsForValue.get(key);
    }

    /**
     * 清除redis缓存
     * @param key 缓存键
     */
    @SuppressWarnings("unchecked")
    public static void redisCacheClear(String key){
        if (StringUtils.isBlank(key)) {
            return;
        }
        RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>)SpringContextUtils.getBean("redisTemplate");
        redisTemplate.delete(key);
    }
}
