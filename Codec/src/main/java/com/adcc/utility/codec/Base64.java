package com.adcc.utility.codec;

/**
 * Base64工具类
 */
public final class Base64 {

    /**
     * Base64加密
     * @param value
     * @return
     */
    public static String encodeBase64(String value) {
        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(value.getBytes(), false));
    }

    /**
     * Base64解密
     * @param value
     * @return
     */
    public static String decodeBase64(String value){
        return new String(org.apache.commons.codec.binary.Base64.decodeBase64(value));
    }
}
