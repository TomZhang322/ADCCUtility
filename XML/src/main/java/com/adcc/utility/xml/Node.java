package com.adcc.utility.xml;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Node类
 */
public class Node extends BaseNode{

    // 节点名称
    private String name = ADCCXML.NODE_NAME;

    // 节点属性
    private Map<String,String> attributes = Maps.newLinkedHashMap();

    // 子节点
    private List<Node> subNodeList = Lists.newArrayList();

    // Unit节点
    private List<Unit> unitList = Lists.newArrayList();

    // 节点值
    private String text = Strings.nullToEmpty("");

    /**
     * 构造函数
     */
    public Node(){

    }

    /**
     * 构造函数
     * @param name
     */
    public Node(String name){
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

    public List<Node> getSubNodeList() {
        return subNodeList;
    }

    public void setSubNodeList(List<Node> subNodeList) {
        this.subNodeList = subNodeList;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 添加子Node节点
     * @param name
     * @param text
     */
    public void addSubNode(String name,String text){
        if(subNodeList.size() > 0){
            for(Node n : subNodeList){
                if(n.getName().equals(name)){
                    return;
                }
            }
        }
        Node node = new Node(name);
        node.setText(text);
        subNodeList.add(node);
    }

    /**
     * 添加子Node节点
     * @param name
     * @param text
     */
    public void addSubNode(String name,Map<String,String> attributes,String text){
        if(subNodeList.size() > 0){
            for(Node n : subNodeList){
                if(n.getName().equals(name)){
                    return;
                }
            }
        }
        Node node = new Node(name);
        node.setAttributes(attributes);
        node.setText(text);
        subNodeList.add(node);
    }

    /**
     * 添加Unit节点
     * @param attributes
     * @param text
     */
    public void addUnit(Map<String,String> attributes,String text){
        Unit unit = new Unit();
        unit.setAttributes(attributes);
        unit.setText(text);
        unitList.add(unit);
    }

    @Override
    protected String formatText(String text) {
        return super.formatText(text);
    }
}
