package com.hsk.task.bean;

public enum ResultEnum {
    //这里是可以自己定义的，方便与前端交互即可
    UNKNOWN_ERROR(-1,"未知错误"),
    SUCCESS(0,"成功"),
    NO_LOGIN(1,"尚未登录"),
    CODE_OVERDUE(2,"验证码已过期"),
    DATA_IS_NULL(3,"数据为空"),
    CODE_ERROR(4,"验证码错误"),
    ;
    private Integer code;
    private String msg;
 
    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
 
    public Integer getCode() {
        return code;
    }
 
    public String getMsg() {
        return msg;
    }
}

