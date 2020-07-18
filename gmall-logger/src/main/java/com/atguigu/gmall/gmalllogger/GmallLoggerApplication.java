package com.atguigu.gmall.gmalllogger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages="com.atguigu.gmall")
@SpringBootApplication
public class GmallLoggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallLoggerApplication.class, args);
    }

}
