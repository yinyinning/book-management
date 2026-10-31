package com.teamdev.bookmanagement;

import com.teamdev.bookmanagement.common.Result;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@MapperScan("com.teamdev.bookmanagement.mapper")
@EnableScheduling
public class BookManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class,args);
    }
    @GetMapping("/api/ping")
    public Result<String> ping(){
        return Result.success("pong");
    }
}
