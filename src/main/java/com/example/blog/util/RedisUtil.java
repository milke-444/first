package com.example.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author blog
 * @date 2024-01-01
 */
@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;  // ✅ 改为非静态

    /**
     * 获取缓存值
     * @param key 缓存键
     * @return 缓存值，不存在返回null
     */
    public String get(String key) {
        if (!StringUtils.hasText(key)) {
//            log.warn("缓存key为空");
            return null;
        }

        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            String value = operations.get(key);

            if (value == null) {
//                log.debug("缓存不存在，key: {}", key);
                return null;
            }

//            log.debug("缓存存在，key: {}, value: {}", key, value);
            return value;

        } catch (Exception e) {
//            log.error("获取缓存失败，key: {}", key, e);
            return null;
        }
    }

    /**
     * 设置缓存（默认过期时间1小时）
     * @param key 缓存键
     * @param value 缓存值
     */
    public void set(String key, String value) {
        set(key, value, 60 * 60, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存（自定义过期时间）
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        if (!StringUtils.hasText(key)) {
//            log.warn("缓存key为空");
            return;
        }

        if (value == null) {
//            log.warn("缓存value为空，key: {}", key);
            return;
        }

        try {
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(key, value, timeout, unit);
//            log.info("缓存设置成功，key: {}, 过期时间: {}{}", key, timeout, unit);

        } catch (Exception e) {
//            log.error("设置缓存失败，key: {}", key, e);
        }
    }

    /**
     * 删除缓存
     * @param key 缓存键
     */
    public void delete(String key) {
        if (!StringUtils.hasText(key)) {
//            log.warn("缓存key为空");
            return;
        }

        try {
            Boolean result = stringRedisTemplate.delete(key);
            if (Boolean.TRUE.equals(result)) {
//                log.info("缓存删除成功，key: {}", key);
            } else {
//                log.debug("缓存不存在，无需删除，key: {}", key);
            }

        } catch (Exception e) {
//            log.error("删除缓存失败，key: {}", key, e);
        }
    }

    /**
     * 判断缓存是否存在
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }

        try {
            Boolean result = stringRedisTemplate.hasKey(key);
            return Boolean.TRUE.equals(result);

        } catch (Exception e) {
//            log.error("判断缓存是否存在失败，key: {}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     * @param key 缓存键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        if (!StringUtils.hasText(key)) {
            return false;
        }

        try {
            Boolean result = stringRedisTemplate.expire(key, timeout, unit);
//            log.debug("设置过期时间成功，key: {}, 过期时间: {}{}", key, timeout, unit);
            return Boolean.TRUE.equals(result);

        } catch (Exception e) {
//            log.error("设置过期时间失败，key: {}", key, e);
            return false;
        }
    }

    // ========== 以下是你的原始方法（修复后） ==========

    /**
     * 获取缓存（兼容你的命名）
     * @deprecated 建议使用 {@link #get(String)} 方法
     */
    @Deprecated
    public String getRedisKeyStorage(String key) {
        return get(key);
    }

    /**
     * 设置缓存（兼容你的命名）
     * @deprecated 建议使用 {@link #set(String, String)} 方法
     */
    @Deprecated
    public void setRedisKeyStorage(String key, String value) {
        set(key, value, 60 * 60, TimeUnit.SECONDS);
    }

    /**
     * 删除缓存（兼容你的命名）
     * @deprecated 建议使用 {@link #delete(String)} 方法
     */
    @Deprecated
    public void deleteRedisKeyStorage(String key) {
        delete(key);
    }
}