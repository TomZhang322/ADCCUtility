package com.adcc.utility.msg620;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaoke on 16/7/20.
 */
public class ACARS620Parser {

    /**
     * 拆解620报文头部
     * @param rawMsg
     * @return
     * @throws ACARS620Exception
     */
    private static String[] parseHead(String rawMsg) throws ACARS620Exception{
        String[] result = null;
        int intIndex = rawMsg.indexOf("\r\n-  ");
        if(intIndex < 0){
            intIndex = rawMsg.indexOf("\n-  ");
        }
        if(intIndex > 0){
            String strHead = rawMsg.substring(0,intIndex);
            result = strHead.replace("\r\n","\n").split("\n");
            if(result.length < 3){
                throw new ACARS620Exception("parse message head error missing lines current " + result.length + " lines");
            }
        }else{
            result = rawMsg.replace("\r\n","\n").split("\n");
            if(result.length < 5){
                throw new ACARS620Exception("parse message head error missing lines current " + result.length + " lines");
            }
        }
        return result;
    }

    /**
     * 解析Line1
     * @param line1
     * @throws ACARS620Exception
     */
    private static Map<String,String> parseLine1(String line1) throws ACARS620Exception{
        Map<String,String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile(Constant.REGEX_ACARS620_LINE1);
        Matcher matcher = pattern.matcher(line1);
        if (matcher.find()) {
            result.put("Priority", matcher.group(2).trim());
            result.put("RecvAddress", matcher.group(3).trim());
        } else {
            throw new ACARS620Exception("parse message line1 error");
        }
        return result;
    }

    /**
     * 解析Line2
     * @param line2
     * @throws ACARS620Exception
     */
    private static Map<String,String> parseLine2(String line2) throws ACARS620Exception{
        Map<String,String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile(Constant.REGEX_ACARS620_LINE2);
        Matcher matcher = pattern.matcher(line2);
        if (matcher.find()) {
            result.put("SendAddress", matcher.group(2).trim());
            result.put("SendTime", matcher.group(3).trim());
        } else {
            throw new ACARS620Exception("parse message line2 error");
        }
        return result;
    }


    /**
     * 解析Line3
     * @param line3
     * @throws ACARS620Exception
     */
    private static Map<String,String> parseLine3(String line3) throws ACARS620Exception{
        Map<String,String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile(Constant.REGEX_ACARS620_LINE3);
        Matcher matcher = pattern.matcher(line3);
        if (matcher.find()) {
            result.put("SMI", matcher.group(2).trim());
        } else {
            throw new ACARS620Exception("parse message line3 error");
        }
        return result;
    }

    /**
     * 解析Line4
     * @param line4
     * @throws ACARS620Exception
     */
    private static Map<String,String> parseLine4(String line4) {
        Map<String,String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile(Constant.REGEX_ACARS620_LINE4);
        Matcher matcher = pattern.matcher(line4);
        while (matcher.find()){
            String[] strValue = matcher.group().split(" ");
            result.put(strValue[0].trim(), strValue[1].trim());
        }
        return result;
    }

    /**
     * 解析Line5
     * @param line5
     * @throws ACARS620Exception
     */
    private static Map<String,String> parseLine5(String line5) {
        Map<String,String> result = Maps.newHashMap();
        Pattern pattern = Pattern.compile(Constant.REGEX_ACARS620_LINE5);
        Matcher matcher = pattern.matcher(line5);
        if (matcher.find()) {
            result.put("DSP", matcher.group(2).trim());
            result.put("RGS", matcher.group(3).trim());
            result.put("RGSTime", matcher.group(4).trim());
            result.put("MSN", matcher.group(5).trim());
        }
        return result;
    }

    /**
     * 解析自由文
     * @param rawMsg
     * @return
     * @throws ACARS620Exception
     */
    private static String parseFreeText(String rawMsg) throws ACARS620Exception{
        int intIndex = rawMsg.indexOf("\r\n-  ");
        if (intIndex >= 0) {
            return rawMsg.substring(intIndex + 4).trim();
        }

        intIndex = rawMsg.indexOf("\n-  ");
        if (intIndex >= 0) {
            return rawMsg.substring(intIndex + 3).trim();
        }
        return null;
    }

    /**
     * 解析ACARS620报文
     * @param rawMsg
     * @return
     */
    public static Optional<ACARS620Msg> parse(String rawMsg) throws ACARS620Exception{
        String[] head = parseHead(rawMsg);
        if(head != null && head.length > 0){
            ACARS620Msg msg = new ACARS620Msg();
            Map<String,String> map = parseLine1(head[0]);
            if(map.containsKey("Priority")){
                msg.setPriority(Strings.nullToEmpty(map.get("Priority")));
            }
            if(map.containsKey("RecvAddress")){
                String[] recvAddress = Strings.nullToEmpty(map.get("RecvAddress")).split(" ");
                msg.setRecvAddress(recvAddress);
            }
            map = parseLine2(head[1]);
            if(map.containsKey("SendAddress")){
                msg.setSendAddress(Strings.nullToEmpty(map.get("SendAddress")));
            }
            if(map.containsKey("SendTime")){
                msg.setSendTime(Strings.nullToEmpty(map.get("SendTime")));
            }
            map = parseLine3(head[2]);
            if(map.containsKey("SMI")){
                msg.setSmi(Strings.nullToEmpty(map.get("SMI")));
            }
            if(head.length >= 4){
                map = parseLine4(head[3]);
                if(map.containsKey("FI")){
                    msg.setFi(Strings.nullToEmpty(map.get("FI")));
                }
                if(map.containsKey("AN")){
                    msg.setAn(Strings.nullToEmpty(map.get("AN")));
                }
            }
            if(head.length >= 5){
                map = parseLine5(head[4]);
                if(map.containsKey("DSP")){
                    msg.setDsp(Strings.nullToEmpty(map.get("DSP")));
                }
                if(map.containsKey("RGS")){
                    msg.setRgs(Strings.nullToEmpty(map.get("RGS")));
                }
                if(map.containsKey("RGSTime")){
                    msg.setRgsTime(Strings.nullToEmpty(map.get("RGSTime")));
                }
                if(map.containsKey("MSN")){
                    msg.setMsn(Strings.nullToEmpty(map.get("MSN")));
                }
            }
            msg.setFreeText(parseFreeText(rawMsg));
            return Optional.of(msg);
        }else{
            return Optional.absent();
        }
    }
}
