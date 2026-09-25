package com.teamdev.bookmanagement.controller;

import com.teamdev.bookmanagement.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/api/hello")
    public Result<String> hello(){
        return Result.success("hello,world");
    }

    @GetMapping("/api/ping")
    public Result<String> ping(){
        return Result.success("pong");
    }
}
