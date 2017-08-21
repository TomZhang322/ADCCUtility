package com.adcc.utility.msg620;

import com.google.common.base.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ACARS620ParserTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testParse() throws Exception {
        String strRawMsg = "\u0001QU XMNMFMF\n" +
                ".BJSXCXA 010159\n" +
                "\u0002DFD\n" +
                "FI MF8045/AN B-5630\n" +
                "DT BJS TAO 010159 D90A\n" +
                "-  TURB-5630 384  98ZSAMZSQD03010158 484   5.8  3.5  -4530.205 584958\n" +
                "0451DESC850 24961544960555 -250 1.51 0.960  5.9  4.4  1.9   1.3  0.1/\n" +
                "/.///  8175N36.23E120.38   1.23126136171  2.7  2.4.+++.+++  6.6 -7.31\n" +
                "0010010010010010010010010101010\n" +
                "\u0003";
        Optional<ACARS620Msg> optional = ACARS620Parser.parse(strRawMsg);
        if(optional.isPresent()){
            ACARS620Msg msg = optional.get();
            System.out.println(msg.getPriority());
            for(String strRecvAddress : msg.getRecvAddress()){
                System.out.println(strRecvAddress);
            }
            System.out.println(msg.getSendAddress());
            System.out.println(msg.getSendTime());
            System.out.println(msg.getSmi());
            System.out.println(msg.getFi());
            System.out.println(msg.getAn());
            System.out.println(msg.getDsp());
            System.out.println(msg.getRgs());
            System.out.println(msg.getRgsTime());
            System.out.println(msg.getMsn());
            System.out.println(msg.getFreeText());
        }
    }
}