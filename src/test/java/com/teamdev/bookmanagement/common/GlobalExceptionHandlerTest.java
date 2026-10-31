package com.teamdev.bookmanagement.common;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler=new GlobalExceptionHandler();
    @Test
    void businessExceptionHandler() {
        ResponseEntity<Result<?>> response=handler.businessExceptionHandler(new BusinessException(400,"用户名已存在"));
        assertEquals(400,response.getStatusCode().value());
        assertEquals(400,response.getBody().getCode());
        assertEquals("用户名已存在",response.getBody().getMessage());
    }

    @Test
    void noResourceFoundExceptionHandler() {
        ResponseEntity<Result<?>> response=handler.noResourceFoundExceptionHandler(mock(NoResourceFoundException.class));
        assertEquals(404,response.getStatusCode().value());
        assertEquals(404,response.getBody().getCode());
        assertEquals("资源不存在",response.getBody().getMessage());
    }

    @Test
    void methodArgumentNotValidExceptionHandler() {
        MethodArgumentNotValidException exception=mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult=mock(BindingResult.class);
        FieldError fieldError=mock(FieldError.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldError()).thenReturn(fieldError);
        when(fieldError.getDefaultMessage()).thenReturn("密码需包含大写字母、小写字母和数字");
        ResponseEntity<Result<?>> response=handler.methodArgumentNotValidExceptionHandler(exception);
        assertEquals(400,response.getStatusCode().value());
        assertEquals("参数校验失败:密码需包含大写字母、小写字母和数字",response.getBody().getMessage());

    }

    @Test
    void notLoginExceptionHandler() {
        NotLoginException exception=mock(NotLoginException.class);
        when(exception.getMessage()).thenReturn("未登录");
        ResponseEntity<Result<?>> response=handler.notLoginExceptionHandler(exception);
        assertEquals(401,response.getStatusCode().value());
        assertEquals(401,response.getBody().getCode());
        assertEquals("未登录或登录已失效",response.getBody().getMessage());
    }

    @Test
    void notRoleExceptionHandler() {
        NotRoleException exception=mock(NotRoleException.class);
        when(exception.getMessage()).thenReturn("admin");
        ResponseEntity<Result<?>> response=handler.notRoleExceptionHandler(exception);
        assertEquals(403,response.getStatusCode().value());
        assertEquals(403,response.getBody().getCode());
        assertEquals("无权限",response.getBody().getMessage());
    }

    @Test
    void exceptionHandler() {
        ResponseEntity<Result<?>> response=handler.exceptionHandler(new RuntimeException("boom"));
        assertEquals(500,response.getStatusCode().value());
        assertEquals(500,response.getBody().getCode());
        assertEquals("出现异常错误，请稍后重试",response.getBody().getMessage());
    }
}