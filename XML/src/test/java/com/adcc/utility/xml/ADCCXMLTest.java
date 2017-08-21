package com.adcc.utility.xml;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;

public class ADCCXMLTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testParseXMLString() throws Exception {
        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<adcc version=\"1\" type=\"UserConfiguration\">\n" +
                "  <userList>\n" +
                "    superNodeText" +
                "    <node id=\"BGS1\" name=\"BGS1\" downlinkForward=\"false\">\n" +
                "      <unit name=\"SendQueue\">Q.BGS1TOGW</unit>\n" +
                "      <unit name=\"RecvQueue\">Q.GWTOBGS1</unit>\n" +
                "      <unit name=\"MsgType\">RawMsg</unit>\n" +
                "      <childNode id=\"Route1\" name=\"Route1\" type=\"Uplink\">\n" +
                "        <unit name=\"SendAddress\">CTUE1YA</unit>\n" +
                "        <unit name=\"RecvAddress\">BJSXCXA,BJSIRXA</unit>\n" +
                "        <unit name=\"Destination\">Q.GWTOBGS2,Q.GWTOBGS3</unit>\n" +
                "        <unit name=\"SMI\">M10,A80,FMD</unit>\n" +
                "        <unit name=\"AN\">******</unit>\n" +
                "        <unit name=\"SpecLabel\" index=\"0\">TEST</unit>\n" +
                "      </childNode>\n" +
                "      <childNode id=\"Route2\" name=\"Route2\" type=\"Downlink\">\n" +
                "        <unit name=\"SendAddress\">BJS****</unit>\n" +
                "        <unit name=\"RecvAddress\">BJSAGCA,XM****F</unit>\n" +
                "        <unit name=\"SMI\">DFD</unit>\n" +
                "        <unit name=\"FI\">MF8045</unit>\n" +
                "        <unit name=\"AN\">B-****</unit>\n" +
                "        <unit name=\"RGS\">***</unit>\n" +
                "        <unit name=\"SpecLabel\" index=\"0\">&#x3;</unit>\n" +
                "      </childNode>\n" +
                "      <childNode id=\"Route3\" name=\"Route3\" type=\"Ground\">\n" +
                "        <unit name=\"Destination\">Q.GWTOBGS2,Q.GWTOBGS3</unit>\n" +
                "        <unit name=\"SpecLabel\" index=\"0\">TEST</unit>\n" +
                "      </childNode>\n" +
                "    </node>\n" +
                "    <node id=\"BGS2\" name=\"BGS2\" downlinkForward=\"false\">\n" +
                "      <unit name=\"SendQueue\">Q.BGS2TOGW</unit>\n" +
                "      <unit name=\"RecvQueue\">Q.GWTOBGS2</unit>\n" +
                "      <unit name=\"MsgType\">RawMsg</unit>\n" +
                "      <childNode id=\"Route2\" name=\"Route2\" type=\"Downlink\">\n" +
                "      </childNode>\n" +
                "    </node>\n" +
                "    <node id=\"BGS3\" name=\"BGS3\" downlinkForward=\"false\">\n" +
                "      <unit name=\"SendQueue\">Q.BGS3TOGW</unit>\n" +
                "      <unit name=\"RecvQueue\">Q.GWTOBGS3</unit>\n" +
                "      <unit name=\"MsgType\">RawMsg</unit>\n" +
                "      <childNode id=\"Route2\" name=\"Route2\" type=\"Downlink\">\n" +
                "      </childNode>\n" +
                "    </node>\n" +
                "  </userList>\n" +
                "</adcc>";

        ADCCXML ixml = ADCCXMLFactory.getInstance().createADCCXML();
        ixml.parseXMLString(str);
        printADCCXML(ixml);
    }

    @Test
    public void testParseXMLFile() throws Exception {
        ADCCXML ixml = new ADCCXMLImpl();
        ixml.parseXMLFile("D:\\ADCCXML_create.xml");
        printADCCXML(ixml);
    }

    @Test
    public void testCreateXMLString() throws Exception {
        ADCCXML ixml = prepareADCCXML();
        System.out.println(ixml.createXMLString());
    }

    @Test
    public void testCreateXMLFile() throws Exception {
        ADCCXML ixml = prepareADCCXML();
        ixml.createXMLFile("D:\\ADCCXML_create.xml", XMLPattern.PRETTY);
    }

    private ADCCXML prepareADCCXML() {
        ADCCXML ixml = ADCCXMLFactory.getInstance().createADCCXML();

        // 设置root
        ixml.setRootName("adcc");
        ixml.getRoot().put("version", "1.0");
        ixml.getRoot().put("type", "UserConfiguration");

        // 设置SuperNode
        SuperNode superNode = new SuperNode("userList");
        ixml.addSuperNode(superNode);
        superNode.setText("testSuperNodeText");

        // 设置Node
        Node bgs1 = new Node("node");
        superNode.getNodeList().add(bgs1);

        // 设置Node的属性
        bgs1.getAttributes().put("id", "BGS1");
        bgs1.getAttributes().put("name", "BGS1");
        bgs1.getAttributes().put("downlinkForward", "false");

        // 设置Node的Unit
        Map<String, String> sendQueue1 = Maps.newHashMapWithExpectedSize(1);
        sendQueue1.put("name", "SendQueue");
        bgs1.addUnit(sendQueue1, "Q.BGS1TOGW");
        Map<String, String> recvQueue1 = Maps.newHashMapWithExpectedSize(1);
        recvQueue1.put("name", "RecvQueue");
        bgs1.addUnit(recvQueue1, "Q.GWTOBGS1");
        Map<String, String> msgType1 = Maps.newHashMapWithExpectedSize(1);
        msgType1.put("name", "MsgType");
        bgs1.addUnit(msgType1, "RawMsg");

        // 设置Node的ChildNode
        Node uplink1 = new Node("childNode");
        bgs1.getSubNodeList().add(uplink1);
        Map<String, String> uplink1Attr = Maps.newHashMapWithExpectedSize(3);
        uplink1Attr.put("id", "Route1");
        uplink1Attr.put("name", "Route1");
        uplink1Attr.put("type", "Uplink");
        uplink1.setAttributes(uplink1Attr);

        // 设置ChildNode的Unit
        Map<String, String> uplink1SendAddress = Maps.newHashMapWithExpectedSize(1);
        uplink1SendAddress.put("name", "SendAddress");
        uplink1.addUnit(uplink1SendAddress, "******");
        Map<String, String> uplink1RecvAddress = Maps.newHashMapWithExpectedSize(1);
        uplink1RecvAddress.put("name", "RecvAddress");
        uplink1.addUnit(uplink1RecvAddress, "******");
        Map<String, String> uplink1Destination = Maps.newHashMapWithExpectedSize(1);
        uplink1Destination.put("name", "Destination");
        uplink1.addUnit(uplink1Destination, "******");
        Map<String, String> uplink1SMI = Maps.newHashMapWithExpectedSize(1);
        uplink1SMI.put("name", "SMI");
        uplink1.addUnit(uplink1SMI, "&#x3;");
        Map<String, String> uplink1AN = Maps.newHashMapWithExpectedSize(1);
        uplink1AN.put("name", "AN");
        uplink1.addUnit(uplink1AN, "******");
        Map<String, String> uplink1SpecLabel = Maps.newHashMapWithExpectedSize(2);
        uplink1SpecLabel.put("name", "SpecLabel");
        uplink1SpecLabel.put("index", "0");
        uplink1.addUnit(uplink1SpecLabel, "TEST");

        Node testChildNode = new Node("childNode");
        bgs1.getSubNodeList().add(testChildNode);
        testChildNode.setText("testChildNode");

        return ixml;
    }

    private void printADCCXML(ADCCXML ixml) {
        System.out.println(ixml.getRootName());
        for(Iterator<Map.Entry<String,String>> iterator = ixml.getRoot().entrySet().iterator();iterator.hasNext();){
            Map.Entry<String,String> entry = iterator.next();
            System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
        }
        for (SuperNode superNode : ixml.getSuperNodeList()) {
            System.out.println(superNode.getName());
            for(Iterator<Map.Entry<String,String>> iterator = superNode.getAttributes().entrySet().iterator();iterator.hasNext();){
                Map.Entry<String,String> entry = iterator.next();
                System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
            }
            if (!Strings.isNullOrEmpty(superNode.getText())) {
                System.out.println("text:" + superNode.getText());
            }
            for (Node node : superNode.getNodeList()) {
                System.out.println(node.getName());
                System.out.println(node.getText());
                for(Iterator<Map.Entry<String,String>> iterator = node.getAttributes().entrySet().iterator();iterator.hasNext();){
                    Map.Entry<String,String> entry = iterator.next();
                    System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
                }
                if (!Strings.isNullOrEmpty(node.getText())) {
                    System.out.println("text:" + node.getText());
                }
                for (Unit unit : node.getUnitList()) {
                    System.out.println(unit.getName());
                    for(Iterator<Map.Entry<String,String>> iterator = unit.getAttributes().entrySet().iterator();iterator.hasNext();){
                        Map.Entry<String,String> entry = iterator.next();
                        System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
                    }
                    if (!Strings.isNullOrEmpty(unit.getText())) {
                        System.out.println("text:" + unit.getText());
                    }
                }
                for (Node subNode : node.getSubNodeList()) {
                    System.out.println(subNode.getName());
                    for(Iterator<Map.Entry<String,String>> iterator = subNode.getAttributes().entrySet().iterator();iterator.hasNext();){
                        Map.Entry<String,String> entry = iterator.next();
                        System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
                    }
                    if (!Strings.isNullOrEmpty(subNode.getText())) {
                        System.out.println("text:" + subNode.getText());
                    }
                    for (Unit unit : subNode.getUnitList()) {
                        System.out.println(unit.getName());
                        for(Iterator<Map.Entry<String,String>> iterator = unit.getAttributes().entrySet().iterator();iterator.hasNext();){
                            Map.Entry<String,String> entry = iterator.next();
                            System.out.println("attribute:" + entry.getKey() + " " + entry.getValue());
                        }
                        if (!Strings.isNullOrEmpty(unit.getText())) {
                            System.out.println("text:" + unit.getText());
                        }
                    }
                }
            }
        }
    }
}