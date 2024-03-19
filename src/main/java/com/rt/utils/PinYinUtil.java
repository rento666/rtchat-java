package com.rt.utils;


import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;


public class PinYinUtil {

    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr){
        return getPinYin(pinYinStr, "", PinyinFormat.WITHOUT_TONE);
    }
    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @param separator 分隔符
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr, String separator){
        return getPinYin(pinYinStr, separator, PinyinFormat.WITHOUT_TONE);
    }
    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @param separator 分隔符
     * @param pinyinFormat 拼音格式 WITH_TONE_MARK（带声调）,WITHOUT_TONE（不带声调） WITH_TONE_NUMBER（带声调数字）
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr, String separator, PinyinFormat pinyinFormat){

        try{
            return PinyinHelper.convertToPinyinString(pinYinStr, separator, pinyinFormat);
        } catch (Exception e){
            System.out.println(e.getMessage());
            return pinYinStr;
        }
    }
    /**
     * 判断一个汉字是否为多音字
     * @param c 汉字
     * @return 判断结果，是汉字返回true，否则返回false
     */
    public static boolean hasMultiPinyin(char c){
        return PinyinHelper.hasMultiPinyin(c);
    }

    /**
     * 将单个汉字转换成带声调格式的拼音
     * @param c 需要转换成拼音的汉字
     * @return 字符串的拼音
     */
    public static String[] convertToPinyinArray(char c){
        return PinyinHelper.convertToPinyinArray(c);
    }
    /**
     * 将单个汉字转换为相应格式的拼音
     * @param c 需要转换成拼音的汉字
     * @param pinyinFormat 拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，WITH_TONE_MARK--带声调
     * @return 汉字的拼音
     */
    public static String[] convertToPinyinArray(char c, PinyinFormat pinyinFormat){
        return PinyinHelper.convertToPinyinArray(c, pinyinFormat);
    }
    /**
     * 繁体字转为简体字
     * @param wordStr 字符串
     * @return 转换后的简体字符串
     */
    public static String convertToSimplifiedChinese(String wordStr){

        return ChineseHelper.convertToSimplifiedChinese(wordStr);
    }
    /**
     * 简体字转换为繁体字
     * @param wordStr 字符串
     * @return 转换后的简体字符串
     */
    public static String convertToTraditionalChinese(String wordStr){
        return ChineseHelper.convertToTraditionalChinese(wordStr);
    }

    /**
     * 判断word是否为简体中文
     * @param word
     * @return true 是，false 否
     */
    public static boolean isChinese(char word){
        return ChineseHelper.isChinese(word);
    }

    /**
     * 判断word是否为繁体字
     * @param word 字符
     * @return true 是，false 否
     */
    public static boolean isTraditionalChinese(char word){
        return ChineseHelper.isTraditionalChinese(word);
    }

    /**
     * 判断字符串是否包含中文
     * @param wordStr 要判断的字符串
     * @return true 包含，false 不包含
     */
    public static boolean containsChinese(String wordStr){
        return ChineseHelper.containsChinese(wordStr);
    }
}
