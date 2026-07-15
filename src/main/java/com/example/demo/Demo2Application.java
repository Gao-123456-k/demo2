package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo2Application {

    // 定义日志对象
    private static final Logger log = LoggerFactory.getLogger(Demo2Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Demo2Application.class, args);

        // 打印不同级别的日志
        log.info("项目启动成功，这是INFO级别日志");
        log.debug("调试日志：DEBUG级别");
        log.warn("警告日志：WARN级别");
        log.error("错误日志：ERROR级别");
    }

}