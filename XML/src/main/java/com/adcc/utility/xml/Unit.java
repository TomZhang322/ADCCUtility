package com.adcc.utility.xml;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Unit类
 */
public class Unit extends BaseNode{

    // 节点名称
    private String name = ADCCXML.UNIT_NAME;

    // 节点属性
    private Map<String,String> attributes = Maps.newLinkedHashMap();

    // 节点值
    private String text;

    /**
     * 构造函数
     */
    public Unit(){

    }

    /**
     * 构造函数
     * @param name
     */
    public Unit(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = formatText(text);
    }

    @Override
    protected String formatText(String text) {
        return super.formatText(text);
    }
}
