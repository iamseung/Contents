package com.example.contents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@EnableCaching
public class ContentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentsApplication.class, args);
    }

}
