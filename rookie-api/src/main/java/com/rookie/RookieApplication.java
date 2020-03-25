package com.rookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
// 使用通用mapper的这个包就是tk开头的这个，需要指明这些包是在哪里
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.rookie.mapper")
public class RookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(RookieApplication.class, args);
    }
}
