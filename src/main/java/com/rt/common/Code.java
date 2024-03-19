package com.rt.common;

public enum Code {
    Success,Error,NotAllow,
    System,User;

    public Integer getCode() {
        switch (this) {
            case Success:
                return 200;
            case Error:
                return 500;
            case NotAllow:
                return 401;
            case System:
                return 20002;
            case User:
                return 30003;
            default:
                throw new RuntimeException("无效的code");
        }
    }
}
