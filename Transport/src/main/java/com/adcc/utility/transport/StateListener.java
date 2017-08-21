package com.adcc.utility.transport;

/**
 * Created by zf on 2017/7/25.
 */
public interface StateListener {

    public void onState(TranState state);

    public void onState(String channelID,TranState state);

    public void onState(String hostName,String channelID,TranState state);
}
