package com.adcc.utility.msg620;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ACARS620报文验证类
 */
public final class ACARS620Verification {

    /**
     * 报文校验，包括报文格式校验和自由文非法字符检验
     * @param rawMsg 报文原文
     * @return true：校验成功；false：校验失败
     * */
    public static boolean verify(String rawMsg) {
        return verifyMsg(rawMsg, -1) == 0;
    }

    /**
     * 报文校验，包括报文格式校验、长度校验和自由文非法字符检验
     * @param rawMsg 报文原文
     * @param length 长度阈值，如果length<=0则不进行长度校验
     * @return true：校验成功；false：校验失败
     * */
    public static boolean verify(String rawMsg, int length) {
        return verifyMsg(rawMsg, length) == 0;
    }

    /**
     * 报文校验，包括报文格式校验、长度校验和自由文非法字符检验
     * @param rawMsg 报文原文
     * @param length 长度阈值，如果length<=0则不进行长度校验
     * @return 0：校验成功；-1：报文格式校验失败；-2：长度校验失败；-3：非法字符检验失败；
     * */
    public static int verifyMsg(String rawMsg, int length) {
        // 正则表达式校验
        Pattern p = Pattern.compile(Constant.REGEX_ACARS620);
        Matcher m = p.matcher(rawMsg);
        if (!m.matches()) {
            return -1;
        }

        // 长度校验
        if (length > 0 && !verifyLength(rawMsg, length)) {
            return -2;
        }

        // 非法字符校验
        if (!verifyChar(rawMsg.toCharArray())) {
            return -3;
        }
        return 0;
    }

    /**
     * 校验message的长度
     * */
    private static boolean verifyLength(String message, int length) {
        if (length < message.length()) {
            return false;
        }
        return true;
    }

    /**
     * 校验message的每个字符
     * */
    private static boolean verifyChar(char[] chars) {
        for (int i=0; i<chars.length; i++) {
            if (!verifyChar(chars[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 单个字符校验
     * */
    private static boolean verifyChar(int ch) {
        // 1 check the highest 1 bit, reject character upper then 127
        if (ch < 0 || ch > 127)
            return false;

        if (ch <= '\003')
            return true;

        // 2 check the next highest 3 bits,
        int h765 = (ch >> 4) & 7;

        // reject if it equals to 000 or 001, unless \x0a and \0x0d (CR, LF)
        if (h765 == 0 || h765 == 1) {
            if (ch == 0x0a || ch == 0x0d) {
                return true;
            }
            return false;
        }

        // \x7f  (DEL) is reserved, not allowed for now
        return ch != 0x7f;
    }
}
