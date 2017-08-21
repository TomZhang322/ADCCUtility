package com.adcc.utility.xml;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ADCCXML2接口实现类
 */
public class ADCCXML2Impl implements ADCCXML2 {

    // 根节点名称
    private String rootName = ROOT_NAME;

    // 字符集
    private String charset = "utf-8";

    // SAXReader
    private SAXReader saxReader = new SAXReader();

    // Root节点
    private Map<String,String> root = Maps.newLinkedHashMap();

    // Node节点
    private List<Node> nodeList = Lists.newArrayList();

    @Override
    public String getRootName() {
        return rootName;
    }

    @Override
    public void setRootName(String rootName) {
        this.rootName = rootName;
    }

    @Override
    public String getCharser() {
        return charset;
    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;
    }

    @Override
    public Map<String, String> getRoot() {
        return root;
    }

    @Override
    public void setRoot(Map<String, String> root) {
        this.root = root;
    }

    @Override
    public List<Node> getNodeList() {
        return nodeList;
    }

    @Override
    public Node getNode(String node) {
        if(!Strings.isNullOrEmpty(node)){
            if(nodeList != null && nodeList.size() > 0){
                for(Node n : nodeList){
                    if(node.equals(n.getName())){
                        return n;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void addNode(Node node) {
        if(node != null){
            if(nodeList == null){
                nodeList = Lists.newArrayListWithExpectedSize(100);
            }
            if(nodeList.size() > 0){
                for(Node n : nodeList){
                    if(node.getName().equals(equals(n.getName()))){
                        return;
                    }
                }
            }
            nodeList.add(node);
        }
    }

    @Override
    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @Override
    public String replaceInvaildChars(String xmlString) throws Exception {
        String strResult = xmlString;
        if(strResult.contains("\001")){
            strResult = strResult.replace("\001","&amp;#x1;");
        }
        if(strResult.contains("&#x1;")) {
            strResult = strResult.replace("&#x1;","&amp;#x1;");
        }
        if(strResult.contains("\002")){
            strResult = strResult.replace("\002","&amp;#x2;");
        }
        if(strResult.contains("&#x2;")) {
            strResult = strResult.replace("&#x2;","&amp;#x2;");
        }
        if(strResult.contains("\003")){
            strResult = strResult.replace("\003","&amp;#x3;");
        }
        if(strResult.contains("&#x3;")) {
            strResult = strResult.replace("&#x3;","&amp;#x3;");
        }
        return strResult;
    }

    @Override
    public void parse(InputStream is) throws Exception{

        // 实例化Document
        Document document = saxReader.read(is);

        // 拆解Root节点
        Element eltRoot = document.getRootElement();
        rootName = eltRoot.getName();
        if(eltRoot.attributes().size() > 0){
            for(Object obj : eltRoot.attributes()){
                Attribute attribute = (Attribute)obj;
                if(attribute != null){
                    root.put(attribute.getName(),attribute.getValue());
                }
            }
        }

        //拆解Node节点
        List<Element> eltList = eltRoot.elements();
        if(eltList != null && eltList.size() > 0){
            for(Element elt : eltList){
                if(elt != null) {
                    Node node = parseNode(elt);
                    if(node != null){
                        nodeList.add(node);
                    }
                }
            }
        }
    }

    /**
     * 拆解Node节点
     * @param elt
     */
    private Node parseNode(Element elt){
        Node node = new Node(elt.getName());
        List<Attribute> attrList = elt.attributes();
        if(attrList != null && attrList.size() > 0){
            for (Attribute attr : attrList){
                if(attr != null){
                    node.getAttributes().put(attr.getName(),attr.getValue());
                }
            }
        }
        List<Element> subEltList = elt.elements();
        if(subEltList != null && subEltList.size() > 0){
            for (Element el : subEltList){
                if (UNIT_NAME.equals(el.getName())) {
                    Unit unit = parseUnit(el);
                    node.getUnitList().add(unit);
                } else {
                    Node childNode = parseNode(el);
                    node.getSubNodeList().add(childNode);
                }
            }
        }
        node.setText(elt.getText());
        return node;
    }

    /**
     * 拆解Unit节点
     * @param elt
     */
    private Unit parseUnit(Element elt){
        Unit unit = new Unit(elt.getName());
        List<Attribute> attrList = elt.attributes();
        if(attrList != null && attrList.size() > 0){
            for (Attribute attr : attrList){
                if(attr != null){
                    unit.getAttributes().put(attr.getName(), attr.getValue());
                }
            }
        }
        unit.setText(elt.getTextTrim());
        return unit;
    }

    @Override
    public void parseXMLString(String xmlString) throws Exception {
        if(!Strings.isNullOrEmpty(xmlString)){
            String strResult = replaceInvaildChars(xmlString);
            InputStream is = new ByteArrayInputStream(strResult.getBytes(Charset.forName(charset)));
            parse(is);
        }
    }

    @Override
    public void parseXMLFile(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        try{
            parse(fis);
        }catch (Exception ex){
            throw ex;
        }finally {
            if(fis != null){
                fis.close();
            }
        }
    }

    @Override
    public Document createXML() throws Exception {

        // 实例化Document4J对象
        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding(charset);

        // 添加Root节点
        Element eltRoot = document.addElement(rootName);
        if(root.size() > 0){
            for(Iterator<Map.Entry<String,String>> iterator = root.entrySet().iterator();iterator.hasNext();){
                Map.Entry<String,String> entry = iterator.next();
                eltRoot.addAttribute(entry.getKey(),entry.getValue());
            }
        }

        // 添加Node节点
        if(nodeList.size() > 0){
            for(Node node : nodeList){
                createNode(eltRoot, node);
            }
        }
        return document;
    }

    /**
     * 添加Node节点
     * */
    private void createNode(Element parentElement, Node node) throws Exception {
        // 在父元素添加Node节点
        Element eltNode = parentElement.addElement(node.getName());

        // 设置Node属性
        if(node.getAttributes().size() > 0){
            for(Iterator<Map.Entry<String,String>> iterator = node.getAttributes().entrySet().iterator();iterator.hasNext();){
                Map.Entry<String,String> entry = iterator.next();
                eltNode.addAttribute(entry.getKey(), entry.getValue());
            }
        }

        // 添加Text
        eltNode.setText(node.getText());

        // 添加Unit节点
        if(node.getUnitList().size() > 0){
            for(Unit unit : node.getUnitList()){
                Element eltUnit = eltNode.addElement(unit.getName());
                if(unit.getAttributes().size() > 0){
                    for(Iterator<Map.Entry<String,String>> iterator = unit.getAttributes().entrySet().iterator();iterator.hasNext();){
                        Map.Entry<String,String> entry = iterator.next();
                        eltUnit.addAttribute(entry.getKey(), entry.getValue());
                    }
                }
                eltUnit.setText(unit.getText());
            }
        }

        // 添加SubNode节点
        if(node.getSubNodeList().size() > 0) {
            for(Node subNode : node.getSubNodeList()){
                createNode(eltNode, subNode);
            }
        }
    }

    @Override
    public String createXMLString() throws Exception {
        return createXML().asXML();
    }

    @Override
    public void createXMLFile(String filePath,XMLPattern pattern) throws Exception {
        Document doc = createXML();
        if(doc != null){
            XMLWriter writer = null;
            if(pattern == XMLPattern.PRETTY){
                writer = new XMLWriter(new FileWriter(filePath), OutputFormat.createPrettyPrint());
            }else if(pattern == XMLPattern.COMPACT){
                writer = new XMLWriter(new FileWriter(filePath), OutputFormat.createCompactFormat());
            }else {
                writer = new XMLWriter(new FileWriter(filePath));
            }
            writer.write(doc);
            writer.close();
        }
    }
}
