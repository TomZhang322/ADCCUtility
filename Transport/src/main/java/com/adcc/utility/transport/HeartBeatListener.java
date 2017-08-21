package com.adcc.utility.transport;

/**
 * Created by zf on 2017/7/25.
 */
public interface HeartBeatListener {

    public boolean sendHearBeatMsg();

    public boolean ping();
}
