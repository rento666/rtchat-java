package com.rt.utils;

import cn.hutool.core.util.ReUtil;

public class Regular {
    public static boolean isValidEmail(String email) {
        // 正则表达式，用于匹配邮箱地址
        String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return isValidDefault(regex, email);
    }

    public static boolean isValidPhone(String phone) {
        return isValidDefault("^1[3-9]\\d{9}$",phone);
    }

    public static boolean isValidHttp(String uri){
        return isValidDefault("^https?$",uri);
    }

    private static boolean isValidDefault(String regex,String value){
        // 使用Hutool的ReUtil进行正则匹配
        return ReUtil.isMatch(regex, value);
    }

}
