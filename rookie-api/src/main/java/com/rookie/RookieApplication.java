package com.rookie;

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
// 使用通用mapper的这个包就是tk开头的这个，需要指明这些包是在哪里
// 扫描 mybatis 通用 mapper 所在的包
@MapperScan(basePackages = "com.rookie.mapper")
// 扫描所有包以及相关组件包
@ComponentScan(basePackages = {"com.rookie", "org.n3r.idworker"})
@ServletComponentScan
//@EnableScheduling       // 开启定时任务
public class RookieApplication {

    public static void main(String[] args) {
        SpringApplication.run(RookieApplication.class, args);
    }

//    // Tomcat Cookie 处理配置 Bean
//    @Bean
//    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
//        return (factory) -> factory.addContextCustomizers(
//                (context) -> context.setCookieProcessor(new LegacyCookieProcessor()));
//    }
}
