package com.iteng.startup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author iteng
 * @date 2023-12-29 16:34
 */
@SpringBootApplication
@MapperScan("com.iteng.startup.mapper")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class);
    }
}
