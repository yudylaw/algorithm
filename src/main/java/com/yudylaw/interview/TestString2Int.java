package com.yudylaw.interview;

/**
 * @author liuyu3@xiaomi.com
 * @since 2016年4月26日
 */

public class TestString2Int {

    public static void main(String[] args) {
        int value = parseInt("-33");
        System.out.println("value="+value);
    }
    
    public static int parseInt(String str) {
        
        if (str == null || str.length() == 0) {
            throw new RuntimeException("str is empty.");
        }
        
        char firstChar = str.charAt(0);
        boolean signed = false;
        if (firstChar == '-') {
            signed = true;
            if (str.length() == 1) {
                throw new RuntimeException("str is not a number.");
            }
        }
        
        int length = str.length();
        int zeroChar = '0';
        int startIndex = signed ? 1 : 0;
        int value = 0;
        
        for (int i = startIndex; i < length; i++) {
            char ch = str.charAt(i);
            if (ch >= '0' && ch <= '9') {
                //ASCII
                int num = (int)ch - zeroChar;
                value += num * Math.pow(10, length - i - 1);
            } else {
                throw new RuntimeException("str is not a number.");
            }
        }
        if (signed) {
            value *=-1;
        }
        return value;
    }

}
