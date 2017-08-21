package com.adcc.utility.msg620;

import com.google.common.base.Strings;

/**
 * ACARS620报文实体
 */
public class ACARS620Msg {

    // 报文等级
    protected String priority = "QU";

    // 接收方地址
    protected String[] recvAddress = new String[]{};

    // 发送方地址
    protected String sendAddress = Strings.nullToEmpty("");

    // 发送时间
    protected String sendTime = Strings.nullToEmpty("");

    // 报文标识
    protected String smi = Strings.nullToEmpty("");

    // 航班号
    protected String fi = Strings.nullToEmpty("");

    // 机尾号
    protected String an = Strings.nullToEmpty("");

    // DSP三码
    private String dsp = Strings.nullToEmpty("");

    // RGS三码
    private String rgs = Strings.nullToEmpty("");

    // RGS站响应时间
    private String rgsTime = Strings.nullToEmpty("");

    // 流水号
    private String msn = Strings.nullToEmpty("");

    // 自由文
    private String freeText = Strings.nullToEmpty("");

    /**
     * 构造函数
     */
    public ACARS620Msg(){

    }

    public String getPriority(){
        return priority;
    }

    public void setPriority(String priority){
        this.priority = priority;
    }

    public String[] getRecvAddress(){
        return recvAddress;
    }

    public void setRecvAddress(String[] recvAddress){
        this.recvAddress = recvAddress;
    }

    public String getSendAddress(){
        return sendAddress;
    }

    public void setSendAddress(String sendAddress){
        this.sendAddress = sendAddress;
    }

    public String getSendTime(){
        return sendTime;
    }

    public void setSendTime(String sendTime){
        this.sendTime = sendTime;
    }

    public String getSmi(){
        return smi;
    }

    public void setSmi(String smi){
        this.smi = smi;
    }

    public String getFi(){
        return fi;
    }

    public void setFi(String fi){
        this.fi = fi;
    }

    public String getAn(){
        return an;
    }

    public void setAn(String an){
        this.an = an;
    }

    public String getDsp(){
        return dsp;
    }

    public void setDsp(String dsp){
        this.dsp = dsp;
    }

    public String getRgs(){
        return rgs;
    }

    public void setRgs(String rgs){
        this.rgs = rgs;
    }

    public String getRgsTime() {
        return rgsTime;
    }

    public void setRgsTime(String rgsTime) {
        this.rgsTime = rgsTime;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }
}
