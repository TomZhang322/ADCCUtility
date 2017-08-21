package com.adcc.utility.msg620;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ACARS620VerificationTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testVerify() throws Exception {
        String rawMsg = "\u0001QU XMNMFMF\n" +
                ".BJSXCXA 010159\n" +
                "\u0002DFD\n" +
                "FI MF8045/AN B-5630\n" +
                "DT BJS TAO 010159 D90A\n" +
                "-  TURB-5630 384  98ZSAMZSQD03010158 484   5.8  3.5  -4530.205 584958\n" +
                "0451DESC850 24961544960555 -250 1.51 0.960  5.9  4.4  1.9   1.3  0.1/\n" +
                "/.///  8175N36.23E120.38   1.23126136171  2.7  2.4.+++.+++  6.6 -7.31\n" +
                "0010010010010010010010010101010\n" +
                "\u0003";

        System.out.println("报文原文校验结果：" + ACARS620Verification.verify(rawMsg));
//        for (char c = '\u0001'; c<'\u001F';) {
//            String temp = rawMsg.replace("384", String.valueOf(c));
//            System.out.println("特殊字符char=" + (int) c + " 校验结果：" + ACARS620Verification.verify(temp));
//
//            c = (char) (c + 1);
//        }
//        String temp = rawMsg.replace("384", String.valueOf('\u007F'));
//        System.out.println("特殊字符char=" + (int) ('\u007F') + " 校验结果：" + ACARS620Verification.verify(temp));
    }
}