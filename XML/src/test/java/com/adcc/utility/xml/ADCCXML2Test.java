package com.adcc.utility.xml;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

public class ADCCXML2Test {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testParseXMLString() throws Exception {
        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?><skylink type=\"ReformatResult\" reality=\"T\" source=\"ACARS\"><head><datagramId>20161101050607M12B-6306M21A</datagramId><gatewayTime>2016-11-01 05:06:07</gatewayTime><priority>QU</priority><an>B-6306</an><fi>CZ6126</fi><rcvAddress>CANXMCZ</rcvAddress><sndAddress>BJSXCXA</sndAddress><bepTime>011305</bepTime><smi>M12</smi><dsp>BJS</dsp><rgs>PEK</rgs><rgsTime>011305</rgsTime><msn>M21A</msn><datagramType>OFF</datagramType><splitTemplateName>1023_CZ_Teledyne_M12_OFF_OFF_A319-100</splitTemplateName><reformatTemplateName>Re_1023_CZ_Teledyne_M12_OFF_OFF_A319-100</reformatTemplateName><bussinessType /></head><data><unit name=\"SENDTIME\">2016-11-01 03:13:07</unit><unit name=\"TKOFIELD\">ZBAA</unit><unit name=\"DESFIELD\">ZYTL</unit><unit name=\"FOB\">16500</unit><unit name=\"OFFTIME\">2016-11-01 03:13:07</unit><unit name=\"LON\">E116374800</unit><unit name=\"LAT\">N40044440</unit></data><regulation><textUnit><unitName>起飞机场:</unitName><length>9</length></textUnit><dataUnit><unitName>TKOFIELD</unitName><length>4</length></dataUnit><asciiUnit><unitName>10 13</unitName><length>2</length></asciiUnit><textUnit><unitName>目的机场:</unitName><length>9</length></textUnit><dataUnit><unitName>DESFIELD</unitName><length>4</length></dataUnit><asciiUnit><unitName>10 13</unitName><length>2</length></asciiUnit><textUnit><unitName>起飞时间:</unitName><length>9</length></textUnit><dataUnit><unitName>OFFTIME</unitName><length>4</length></dataUnit><asciiUnit><unitName>10 13</unitName><length>2</length></asciiUnit><textUnit><unitName>经度:</unitName><length>5</length></textUnit><dataUnit><unitName>LAT</unitName><length>8</length></dataUnit><asciiUnit><unitName>10 13</unitName><length>2</length></asciiUnit><textUnit><unitName>维度:</unitName><length>5</length></textUnit><dataUnit><unitName>LON</unitName><length>8</length></dataUnit><asciiUnit><unitName>10 13</unitName><length>2</length></asciiUnit><textUnit><unitName>油量:</unitName><length>5</length></textUnit><dataUnit><unitName>FOB</unitName><length>5</length></dataUnit></regulation><msg>&#x1;QU CANXMCZ\n" +
                ".BJSXCXA 011305\n" +
                "&#x2;M12\n" +
                "FI CZ6126/AN B-6306\n" +
                "DT BJS PEK 011305 M21A\n" +
                "-  OFF01CSN6126/--010313ZBAAZYTL\n" +
                "0313 165\n" +
                "LON E116.630,LAT N 40.079\n" +
                "629,0,\n" +
                "&#x3;</msg></skylink>";

        ADCCXML2 ixml = ADCCXMLFactory.getInstance().createADCCXML2();
        ixml.parseXMLString(str);
        printADCCXML2(ixml);
    }

    @Test
    public void testParseXMLFile() throws Exception {
        ADCCXML2 ixml = new ADCCXML2Impl();
        ixml.parseXMLFile("D:\\ADCCXML2_create.xml");
        printADCCXML2(ixml);
    }

    @Test
    public void testCreateXMLString() throws Exception {
        ADCCXML2 ixml = prepareADCCXML2();
        System.out.println(ixml.createXMLString());
    }

    @Test
    public void testCreateXMLFile() throws Exception {
        ADCCXML2 ixml = prepareADCCXML2();
        ixml.createXMLFile("D:\\ADCCXML2_create.xml", XMLPattern.NORMAL);
    }

    private ADCCXML2 prepareADCCXML2() {
        ADCCXML2 ixml = ADCCXMLFactory.getInstance().createADCCXML2();

        // 设置root
        ixml.setRootName("skylink");
        ixml.getRoot().put("type", "ReformatResult");
        ixml.getRoot().put("reality", "T");
        ixml.getRoot().put("source", "ACARS");

        // 设置node及subNode
        Node head = new Node("head");
        head.addSubNode("datagramId", "20160902084308DFDB-5736D55A");
        head.addSubNode("gatewayTime", "2016-09-02 08:43:08");
        head.addSubNode("priority", "");

        Node subNode1 = new Node("SubNode1");
        head.getSubNodeList().add(subNode1);
        Map<String, String> subNode1Unit = Maps.newHashMapWithExpectedSize(3);
        subNode1Unit.put("name", "testUnit");
        subNode1.addUnit(subNode1Unit, "XXXXX");

        Node subNode2 = new Node("SubNode2");
        subNode1.getSubNodeList().add(subNode2);
        Node subNode3 = new Node("SubNode3");
        subNode2.getSubNodeList().add(subNode3);

        // 设置node及unit
        Node data = new Node("data");
        Map<String, String> attributes = Maps.newHashMapWithExpectedSize(3);
        attributes.put("name", "BLD2MEGT");
        data.addUnit(attributes, "&#x3;");

        // 设置node及text
        Node msg = new Node("msg");
        msg.setText("&#x1;QU SZXUOZH ILNGE7X\n" +
                ".BJSXCXA 020543\n" +
                "&#x2;DFD\n" +
                "FI ZH9367/AN B-5736\n" +
                "DT BJS CKG 020543 D55A\n" +
                "-  TKOB-5736 6681036ZUCKZSNJ020905423389  30.0  25.5  27530.273 67603\n" +
                "93671TOFFD01 01962828962839 94.6 94.7 98.8 99.0823841 3707 3740  72.9\n" +
                "  73.25859 92 930.290.270.300.292502760.080.080.110.11283331  0.0  0.\n" +
                "0\n" +
                "   0   0 0.00 0.00110LO LO 00011001557171\n" +
                "\n" +
                "&#x3;");

        ixml.getNodeList().add(head);
        ixml.getNodeList().add(data);
        ixml.getNodeList().add(msg);
        return ixml;
    }

    private void printADCCXML2(ADCCXML2 ixml) {
        System.out.println(ixml.getRootName());
        for(Iterator<Map.Entry<String,String>> iterator = ixml.getRoot().entrySet().iterator();iterator.hasNext();){
            Map.Entry<String,String> entry = iterator.next();
            System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
        }
        for (Node node : ixml.getNodeList()) {
            printADCCXML2Node(node);
        }
    }

    private void printADCCXML2Node(Node node) {
        // 输出Name
        System.out.println(node.getName());
        // 输出Attributes
        for(Iterator<Map.Entry<String,String>> iterator = node.getAttributes().entrySet().iterator();iterator.hasNext();){
            Map.Entry<String,String> entry = iterator.next();
            System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
        }
        // 输出Text
        if (!Strings.isNullOrEmpty(node.getText()) && node.getText().trim().length() > 0) {
            System.out.println("text:" + node.getText().trim());
        }
        // 输出Unit
        for (Unit unit : node.getUnitList()) {
            System.out.println(unit.getName());
            for(Iterator<Map.Entry<String,String>> iterator = unit.getAttributes().entrySet().iterator();iterator.hasNext();){
                Map.Entry<String,String> entry = iterator.next();
                System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
            }
            if (!Strings.isNullOrEmpty(unit.getText()) && unit.getText().trim().length() > 0) {
                System.out.println("text:" + unit.getText());
            }
        }
        // 输出subNode
        for (Node subNode : node.getSubNodeList()) {
            printADCCXML2Node(subNode);
        }
    }
}