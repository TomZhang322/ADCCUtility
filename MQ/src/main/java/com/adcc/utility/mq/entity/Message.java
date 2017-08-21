package com.adcc.utility.mq.entity;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 消息实体
 */
public class Message implements Serializable{

    // 消息头
    private Map<String,Object> head = Maps.newHashMapWithExpectedSize(10);

    // 消息体
    private byte[] content;

    // 消息体长度
    private long length;

    // 时间戳
    private String timestamp;

    /**
     * 构造函数
     */
    public Message(){
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 构造函数
     */
    public Message(byte[] content){
        this.content = content;
        length = content.length;
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    /**
     * 构造函数
     * @param head
     * @param content
     */
    public Message(Map<String,Object> head,byte[] content){
        this.head = head;
        this.content = content;
        length = content.length;
        timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public Map<String,Object> getHead() {
        return head;
    }

    public void setHead(Map<String,Object> head) {
        this.head = head;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
        length = content.length;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return Strings.nullToEmpty(new String(content,0,content.length));
    }
}
