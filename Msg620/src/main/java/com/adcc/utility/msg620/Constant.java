package com.adcc.utility.msg620;

/**
 * 常量类
 */
public class Constant {

    /**
     * Line1正则表达式
     */
    public static final String REGEX_ACARS620_LINE1 = "(\\x01)([A-Z]{2})((?:\\s+\\S{7}){1,16})";

    /**
     * Line2正则表达式
     */
    public static final String REGEX_ACARS620_LINE2 = "(\\.)(\\S{7}{1})((?:\\s{1}\\d{6})?)";

    /**
     * Line3正则表达式
     */
    public static final String REGEX_ACARS620_LINE3 = "(\\x02)(\\S{3})";

    /**
     * Line4正则表达式
     */
    public static final String REGEX_ACARS620_LINE4 = "((?:FI|AN|GL|AP|TP|MA|AD|OT|OF|ON|IN|FB|DS)\\s{1}[\\S&&[^/]]+)";

    /**
     * Line5正则表达式
     */
    public static final String REGEX_ACARS620_LINE5 = "(DT)((?:\\s{1}\\S+))((?:\\s{1}\\S+))((?:\\s{1}\\d{6}))((?:\\s{1}\\S+))";

    /**
     * 620报文正则表达式
     */
//    public static final String REGEX_ACARS620 = "\\x01[A-Z]{2}(?:\\s+\\S{7}){1,16}(?:\\r|\\n|\\r\\n)[.]\\S+(?:\\s+(?:\\d{6})(?:\\s+(\\S+))?)?(?:\\r|\\n|\\r\\n)\\x02\\S+(?:\\r|\\n|\\r\\n)((.+(?:\\r|\\n|\\r\\n))*)(?:DT\\s+.+(?:\\r|\\n|\\r\\n))?(?:-\\s\\s(?:.*(?:\\r|\\n|\\r\\n))*)?\\x03";

    public static final String REGEX_ACARS620 = "\\x01[A-Z]{2}(?:\\s+\\S{7}){1,16}(?:\\n|\\r\\n)[.]\\S+(?:\\s+\\d{6}(?:\\s+\\S+)?)?(?:\\n|\\r\\n)\\x02\\S+(?:\\n|\\r\\n)(?:[\\s\\S]*)\\x03";
}
