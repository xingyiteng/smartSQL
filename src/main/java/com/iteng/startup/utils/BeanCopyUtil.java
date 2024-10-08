package com.iteng.startup.utils;

import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Bean拷贝工具类
 *
 * @author iteng
 * @date 2024-01-05 18:59
 */
@Slf4j
public class BeanCopyUtil {

    public static <T> T copyProperties(Object source, Class<T> target) {
        try {
            T t = target.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            log.error("copyProperties()转换出错", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    public static <T> List<T> copyWithCollection(List<?> sourceList, Class<T> target) {
        try {
            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("copyWithCollection()转换出错", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    public static <T> Set<T> copyWithCollection(Set<?> sourceList, Class<T> target) {
        try {
            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("copyWithCollection()转换出错", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
}
