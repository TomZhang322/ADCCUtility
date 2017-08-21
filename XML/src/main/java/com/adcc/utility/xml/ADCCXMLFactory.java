package com.adcc.utility.xml;

/**
 * ADCCXML工厂
 */
public class ADCCXMLFactory extends XMLFactory{

    // 单例对象
    private static ADCCXMLFactory instance;

    /**
     * 构造函数
     */
    private ADCCXMLFactory(){

    }

    /**
     * 单例方法
     * @return
     */
    public static XMLFactory getInstance(){
        if(instance == null){
            instance = new ADCCXMLFactory();
        }
        return instance;
    }

    @Override
    public ADCCXML createADCCXML() {
        return new ADCCXMLImpl();
    }

    @Override
    public ADCCXML2 createADCCXML2() {
        return new ADCCXML2Impl();
    }
}
