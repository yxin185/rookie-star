package com.rookie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
// 使用通用mapper的这个包就是tk开头的这个，需要指明这些包是在哪里
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.rookie.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.rookie", "org.n3r.idworker"})
//@EnableScheduling       // 开启定时任务
public class RookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(RookieApplication.class, args);
    }
}
