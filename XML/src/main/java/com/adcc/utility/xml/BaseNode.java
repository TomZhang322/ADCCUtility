package com.adcc.utility.xml;

/**
 * Created by zyy on 2016/10/13.
 */
public abstract class BaseNode {

    /**
     * 格式化文本
     * @param text
     * @return
     */
    protected String formatText(String text){
        String strText = text.replace("\001","&#x1;").replace("\002","&#x2;").replace("\003","&#x3");
        return strText;
    }
}
