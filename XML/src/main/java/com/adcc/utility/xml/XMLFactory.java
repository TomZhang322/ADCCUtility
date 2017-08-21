package com.adcc.utility.xml;

/**
 * XML抽象工厂
 */
public abstract class XMLFactory{

    /**
     * 创建ADCCXML
     * @return
     */
    public abstract ADCCXML createADCCXML();

    /**
     * 创建ADCCXML2
     * @return
     */
    public abstract ADCCXML2 createADCCXML2();

}
