package com.adcc.utility.codec;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * MD5工具类
 */
public final class MD5 {

    /**
     * 计算MD5值
     * @param buffer
     * @return
     */
    public static String getMD5(ByteBuffer buffer) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buffer);
        BigInteger bi = new BigInteger(1, md5.digest());
        return bi.toString(16);
    }

    /**
     * 计算MD5值
     * @param buffer
     * @return
     * @throws Exception
     */
    public static String getMD5(String buffer) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buffer.getBytes());
        BigInteger bi = new BigInteger(1, md5.digest());
        return bi.toString(16);
    }

    /**
     * 计算MD5值
     * @param buffer
     * @return
     * @throws Exception
     */
    public static String getMD5(byte[] buffer) throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(buffer);
        BigInteger bi = new BigInteger(1, md5.digest());
        return bi.toString(16);
    }

    /**
     * 计算MD5值
     * @param file
     * @return
     * @throws Exception
     */
    public static String getMD5(File file) throws Exception{
        FileInputStream fis  = new FileInputStream(file);
        try{
            FileChannel channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate((int) file.length());
            channel.read(buffer);
            channel.close();
            buffer.position(0);
            return getMD5(buffer);
        }catch (Exception ex){
            throw ex;
        }finally {
            if(fis != null){
                fis.close();
            }
        }
    }


    /**
     * MD5密码加密
     * @param password
     * @param salt
     * @return
     */
    public static String encodePassword(String password, Object salt) {
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
        return md5PasswordEncoder.encodePassword(password, salt);
    }

    /**
     * 验证密码是否正确
     * @param encPassword
     * @param rawPassword
     * @param salt
     * @return
     */
    public static boolean isPasswordValid(String encPassword,String rawPassword, String salt){
        Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();
        return md5PasswordEncoder.isPasswordValid(encPassword, rawPassword, salt);
    }
}
