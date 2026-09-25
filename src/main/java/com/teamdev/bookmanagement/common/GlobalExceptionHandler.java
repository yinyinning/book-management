package com.teamdev.bookmanagement.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<?>> businessExceptionHandler(BusinessException businessException){
        log.warn("业务异常:{}",businessException.getMessage());
        return ResponseEntity.status(businessException.getCode()).body(Result.error(businessException.getCode(),businessException.getMessage()));
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<?>> noResourceFoundExceptionHandler(NoResourceFoundException noResourceFoundException){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404,"资源不存在"));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> exceptionHandler(Exception exception){
        log.error("未知异常",exception);
        return ResponseEntity.status(500).body(Result.error(500,"出现异常错误，请稍后重试"));
    }
}
