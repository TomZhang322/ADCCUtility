package com.adcc.utility.xml;

import org.dom4j.Document;

import java.io.InputStream;

/**
 * BaseXML
 */
public interface BaseXML {

    public final static String STRING_INVAILD_TEMPLATE = "<![CDATA[%s]]>";

    // 非法字符正则表达式
    public static String[] STRING_INVAILD_CHARS = new String[]{"&#x1;","&#x2;","&#x3;","\001","\002","\003"};

    /**
     * 替换非法字符
     * @param xmlString
     * @return
     * @throws Exception
     */
    public String replaceInvaildChars(String xmlString) throws Exception;

    /**
     * 拆解XML
     * @param is
     * @throws Exception
     */
    public void parse(InputStream is) throws Exception;

    /**
     * 拆解XML字符串
     * @param xmlString
     * @throws Exception
     */
    public void parseXMLString(String xmlString) throws Exception;

    /**
     * 拆解XML文件
     * @param filePath
     * @throws Exception
     */
    public void parseXMLFile(String filePath) throws Exception;

    /**
     * 创建XML
     * @return
     */
    public Document createXML() throws Exception;

    /**
     * 创建XML字符串
     * @return
     * @throws Exception
     */
    public String createXMLString() throws Exception;

    /**
     * 创建XML文件
     * @param filePath
     * @param pattern
     */
    public void createXMLFile(String filePath,XMLPattern pattern) throws Exception;
}
