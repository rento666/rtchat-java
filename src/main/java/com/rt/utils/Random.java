package com.rt.utils;

import cn.hutool.core.util.RandomUtil;

public class Random {

    private static final String[] SURNAMES = {
            "独特的", "聪明的", "快乐的", "勇敢的", "美丽的",
            "迷人的", "激动的", "和谐的", "幸福的", "神秘的",
            "温柔的", "魅力的", "奇异的", "灵动的", "欢快的"
    };

    private static final String[] NAMES = {
            "猫", "雨", "花", "阳光", "海", "风", "星星", "梦想", "音乐", "舞蹈",
            "冒险者", "冰淇淋", "小溪", "彩虹", "书籍", "绘画", "微笑", "诗人", "宇航员", "幽灵"
    };

    public static String genRandomUsername() {
        java.util.Random random = new java.util.Random();
        String surname = SURNAMES[random.nextInt(SURNAMES.length)];
        String name = NAMES[random.nextInt(NAMES.length)];
        return surname + name;
    }

    public static String genRandomNumber(Integer number) {
        return RandomUtil.randomNumbers(number);
    }

    public static String genRandomStr(Integer number) {
        return RandomUtil.randomString(number);
    }

}
