package com.adcc.utility.xml;

import java.util.List;
import java.util.Map;

/**
 * ADCCXML2接口
 */
public interface ADCCXML2 extends BaseXML{

    /**
     * Root节点名称
     */
    public static String ROOT_NAME = "adcc";

    /**
     * Unit节点名称
     */
    public static String UNIT_NAME = "unit";

    /**
     * 取得root节点名称
     * @return
     */
    public String getRootName();

    /**
     * 设置root节点名称
     * @param rootName
     */
    public void setRootName(String rootName);

    /**
     * 取得字符集
     * @return
     */
    public String getCharser();

    /**
     * 设置字符集
     * @param charset
     */
    public void setCharset(String charset);

    /**
     * 取得Root节点
     * @return
     */
    public Map<String,String> getRoot();

    /**
     * 设置Root节点
     * @param root
     */
    public void setRoot(Map<String,String> root);

    /**
     * 设置NodeList
     * @param nodeList
     */
    public void setNodeList(List<Node> nodeList);

    /**
     * 取得NodeList
     * @return
     */
    public List<Node> getNodeList();

    /**
     * 取得Node节点
     * @param node
     * @return
     */
    public Node getNode(String node);

    /**
     * 添加Node节点
     * @param node
     */
    public void addNode(Node node);
}
