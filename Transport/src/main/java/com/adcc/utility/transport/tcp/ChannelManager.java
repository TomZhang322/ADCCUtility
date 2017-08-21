package com.adcc.utility.transport.tcp;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * Created by zf on 2017/8/10.
 */
public class ChannelManager {

    private HashMap<String,Channel> channels = new HashMap<>();

    public void addChannel(String channelId, Channel channel){
        channels.put(channelId,channel);
    }

    public void removeChannel(String channelId){
        channels.remove(channelId);
    }

    public Channel getChannel(String channelId){
        return channels.get(channelId);
    }

    public HashMap<String, Channel> getChannels() {
        return channels;
    }
}
