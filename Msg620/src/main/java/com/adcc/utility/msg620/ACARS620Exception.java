package com.adcc.utility.msg620;

/**
 * ACARS620报文异常类
 */
public class ACARS620Exception extends Exception{

    private String msg;

    private Throwable cause;

    /**
     * 构造函数
     * @param msg
     */
    public ACARS620Exception(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 构造函数
     * @param msg
     * @param cause
     */
    public ACARS620Exception(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
        this.cause = cause;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
