package com.w.t.conductor.util;

import java.util.Random;

public class RandomCodeGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 2;

    private static String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }

    /**
     * 10进制转62进制
     */
    private static String longTo62() {
        long num = System.currentTimeMillis();
        // StringBuffer线程安全，StringBuilder线程不安全
        StringBuffer sb = new StringBuffer();
        do {
            int i = (int) (num % 62);
            sb.append(CHARACTERS.charAt(i));
            num = num / 62;
        } while (num > 0);
        return sb.reverse().substring(2, 5);
    }


    /**
     * 获取随机码
     *
     * @return
     */
    public static String getRandomWithTimstamp() {
        return "_" + generateRandomCode() + longTo62() + generateRandomCode();
    }
}