package com.teamdev.bookmanagement.common;

public class BusinessException extends RuntimeException{
    private Integer code;
    public BusinessException(Integer code,String msg){
        super(msg);
        this.code=code;
    }
    public Integer getCode(){
        return code;
    }
}
