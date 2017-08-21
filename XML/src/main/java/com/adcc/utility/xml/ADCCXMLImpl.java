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
 * ADCCXML接口实现类
 */
public class ADCCXMLImpl implements ADCCXML {

    // 根节点名称
    private String rootName = ROOT_NAME;

    // 字符集
    private String charset = "utf-8";

    // SAXReader
    private SAXReader saxReader = new SAXReader();

    // Root节点
    private Map<String,String> root = Maps.newLinkedHashMap();

    // SuperNode节点
    private List<SuperNode> superNodeList = Lists.newArrayList();

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
    public void setSuperNodeList(List<SuperNode> superNodeList) {
        this.superNodeList = superNodeList;
    }

    @Override
    public List<SuperNode> getSuperNodeList() {
        return superNodeList;
    }

    @Override
    public void addSuperNode(SuperNode node) {
        if(superNodeList.size() > 0){
            for(SuperNode sn : superNodeList){
                if(sn.getName().equals(node.getName())){
                    return;
                }
            }
        }
        superNodeList.add(node);
    }

    @Override
    public SuperNode getSuperNode(String nodeName) {
        if(superNodeList.size() > 0){
            for(SuperNode sn : superNodeList){
                if(sn.getName().equals(nodeName)){
                    return sn;
                }
            }
        }
        return null;
    }

    @Override
    public void setNodeList(List<Node> nodeList) {

    }

    @Override
    public List<Node> getNodeList() {
        return null;
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
    public void parse(InputStream is) throws Exception {

        // 实例化Document
        Document document = saxReader.read(is);

        // 拆解Root节点
        Element eltRoot = document.getRootElement();

        // 拆解Root节点
        parseRoot(eltRoot);

        // 拆解SuperNode节点
        List<Element> eltList = eltRoot.elements();
        if(eltList != null && eltList.size() > 0){
            for(Element elt : eltList){
                if(elt != null) {
                    SuperNode superNode  = parseSuperNode(elt);
                    if(superNode != null){
                        superNodeList.add(superNode);
                    }
                }
            }
        }
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

        // 添加SuperNode节点
        if(superNodeList.size() > 0){
            for(SuperNode superNode : superNodeList){
                Element eltSuperNode = eltRoot.addElement(superNode.getName());
                if(superNode.getAttributes().size() > 0){
                    for(Iterator<Map.Entry<String,String>> iterator = superNode.getAttributes().entrySet().iterator();iterator.hasNext();){
                        Map.Entry<String,String> entry = iterator.next();
                        eltSuperNode.addAttribute(entry.getKey(),entry.getValue());
                    }
                }
                if(superNode.getNodeList().size() > 0){
                    for(Node node : superNode.getNodeList()){
                        Element eltNode = eltSuperNode.addElement(node.getName());
                        if(node.getAttributes().size() > 0){
                            for(Iterator<Map.Entry<String,String>> iterator = node.getAttributes().entrySet().iterator();iterator.hasNext();){
                                Map.Entry<String,String> entry = iterator.next();
                                eltNode.addAttribute(entry.getKey(), entry.getValue());
                            }
                        }
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
                        if(node.getSubNodeList().size() > 0){
                            for(Node subNode : node.getSubNodeList()){
                                Element eltSubNode = eltNode.addElement(subNode.getName());
                                if(subNode.getAttributes().size() > 0){
                                    for(Iterator<Map.Entry<String,String>> iterator = subNode.getAttributes().entrySet().iterator();iterator.hasNext();){
                                        Map.Entry<String,String> entry = iterator.next();
                                        eltSubNode.addAttribute(entry.getKey(), entry.getValue());
                                    }
                                }
                                if(subNode.getUnitList().size() > 0){
                                    for(Unit unit : subNode.getUnitList()){
                                        Element eltUnit = eltSubNode.addElement(unit.getName());
                                        if(unit.getAttributes().size() > 0){
                                            for(Iterator<Map.Entry<String,String>> iterator = unit.getAttributes().entrySet().iterator();iterator.hasNext();){
                                                Map.Entry<String,String> entry = iterator.next();
                                                eltUnit.addAttribute(entry.getKey(), entry.getValue());
                                            }
                                        }
                                        eltUnit.setText(unit.getText());
                                    }
                                }else{
                                    if(!Strings.isNullOrEmpty(subNode.getText())){
                                        eltSubNode.setText(subNode.getText());
                                    }
                                }
                            }
                        }
                        if(node.getUnitList().size() == 0 && node.getSubNodeList().size() == 0){
                            if(!Strings.isNullOrEmpty(node.getText())){
                                eltNode.setText(node.getText());
                            }
                        }
                    }
                }else{
                    if(!Strings.isNullOrEmpty(superNode.getText())){
                        eltSuperNode.setText(superNode.getText());
                    }
                }
            }
        }
        return document;
    }

    @Override
    public String createXMLString() throws Exception {
        return createXML().asXML();
    }

    @Override
    public void createXMLFile(String filePath, XMLPattern pattern) throws Exception {
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

    /**
     * 拆解Root节点
     * @param element
     */
    private void parseRoot(Element element){
        rootName = element.getName();
        if(element.attributes().size() > 0){
            for(Object obj : element.attributes()){
                Attribute attribute = (Attribute)obj;
                if(attribute != null){
                    root.put(attribute.getName(),attribute.getValue());
                }
            }
        }
    }

    /**
     * 拆解SuperNode节点
     * @param elt
     */
    private SuperNode parseSuperNode(Element elt){

        // 拆解SuperNode节点属性
        SuperNode superNode = new SuperNode(elt.getName());
        superNode.setText(elt.getTextTrim());
        List<Attribute> attributeList = elt.attributes();
        if(attributeList != null && attributeList.size() > 0){
            for (Attribute attribute : attributeList){
                if(attribute != null){
                    superNode.getAttributes().put(attribute.getName(),attribute.getValue());
                }
            }
        }

        // 拆解Node节点
        List<Element> nodeList = elt.elements();
        if(nodeList != null && nodeList.size() > 0){
            for(Element el : nodeList){
                Node node = parseNode(el);
                if(node != null){
                    superNode.getNodeList().add(node);
                }
            }
        }
        return superNode;
    }

    /**
     * 拆解Node节点
     * @param elt
     */
    private Node parseNode(Element elt){
        Node node = null;
        if(NODE_NAME.equals(elt.getName())){
            node = new Node(NODE_NAME);
            node.setText(elt.getTextTrim());
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
                    if(UNIT_NAME.equals(el.getName())){
                        Unit unit = parseUnit(el);
                        node.getUnitList().add(unit);
                    }else if(CHILD_NODE_NAME.equals(el.getName())){
                        Node childNode = parseChildNode(el);
                        node.getSubNodeList().add(childNode);
                    }
                }
            }
        }
        return node;
    }

    /**
     * 拆解Unit节点
     * @param elt
     */
    private Unit parseUnit(Element elt){
        Unit unit = new Unit(UNIT_NAME);
        unit.setText(elt.getTextTrim());
        List<Attribute> attrList = elt.attributes();
        if(attrList != null && attrList.size() > 0){
            for (Attribute attr : attrList){
                if(attr != null){
                    unit.getAttributes().put(attr.getName(), attr.getValue());
                }
            }
        }
        return unit;
    }

    private Node parseChildNode(Element elt){
        Node node = new Node(CHILD_NODE_NAME);
        node.setText(elt.getTextTrim());
        List<Attribute> attrList = elt.attributes();
        if (attrList != null && attrList.size() > 0) {
            for (Attribute attr : attrList) {
                if (attr != null) {
                    node.getAttributes().put(attr.getName(), attr.getValue());
                }
            }
        }
        List<Element> subEltList = elt.elements();
        if (subEltList != null && subEltList.size() > 0) {
            for (Element el : subEltList) {
                if (UNIT_NAME.equals(el.getName())) {
                    Unit unit = parseUnit(el);
                    node.getUnitList().add(unit);
                }
            }
        }
        return node;
    }
}
