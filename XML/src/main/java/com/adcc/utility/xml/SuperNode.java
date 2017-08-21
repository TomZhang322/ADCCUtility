package com.adcc.utility.xml;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * SuperNode类
 */
public class SuperNode {

    // 节点名称
    private String name = Strings.nullToEmpty("");

    // 节点属性
    private Map<String,String> attributes = Maps.newLinkedHashMap();

    // Node节点
    private List<Node> nodeList = Lists.newArrayList();

    // 节点值
    private String text = Strings.nullToEmpty("");

    /**
     * 构造函数
     */
    public SuperNode(){

    }

    /**
     * 构造函数
     * @param name
     */
    public SuperNode(String name){
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

    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
