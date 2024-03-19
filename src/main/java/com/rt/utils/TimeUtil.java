package com.rt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TimeUtil {
    public static String getYYYYMMDD(){
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 定义日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期
        return currentDate.format(formatter);
    }

    public static Long getTimeMillis(){
        return System.currentTimeMillis();
    }

    public static String now(){
        LocalDateTime time = LocalDateTime.now();
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String format(LocalDateTime time){
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取从某天开始到当前经过了多少天
      */
    public static String getFromToDay(String inputDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(inputDate, formatter);
        long daysSince = ChronoUnit.DAYS.between(date, LocalDate.now());
        return daysSince + "天";
    }

    /**
     * t1是不是比t2新？
     * 例如：t1为 23年，t2为22年，则为true
     * @return 是不是最新时间
     */
    public static Boolean isLeast(String t1,String t2){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = dateFormat.parse(t1);
            Date date2 = dateFormat.parse(t2);
            if (date1.compareTo(date2) < 0) {
                // t2最新
                return false;
            } else if (date1.compareTo(date2) > 0) {
                // t1最新
                return true;
            } else {
                // 时间相等
                return true;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
