package com.adcc.utility.codec;

public class MD5Test {

    String salt = "ADCCADCC";
    String rawPassword = "123456";
    String encPassword = "963ed50bfa90c82c996cc1bd9e620a9a";

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testGetMD5() throws Exception {

    }

    @org.junit.Test
    public void testGetMD51() throws Exception {

    }

    @org.junit.Test
    public void testGetMD52() throws Exception {

    }

    @org.junit.Test
    public void testGetMD53() throws Exception {

    }

    @org.junit.Test
    public void testEncodePassword() throws Exception {
        System.out.println(MD5.encodePassword(rawPassword, salt));
    }

    @org.junit.Test
    public void testIsPasswordValid() throws Exception {
        System.out.println(MD5.isPasswordValid(encPassword,rawPassword,salt));
    }
}