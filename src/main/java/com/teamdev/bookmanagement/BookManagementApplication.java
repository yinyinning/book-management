package com.teamdev.bookmanagement;

import com.teamdev.bookmanagement.common.Result;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class BookManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookManagementApplication.class,args);
    }
    @GetMapping("/api/ping")
    public Result<String> ping(){
        return Result.success("pong");
    }
}
