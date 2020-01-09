package com.tjq.triple.common.utils;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * 地址解析工具
 *
 * @author tjq
 * @since 2020/1/8
 */
public class AddressUtils {

    /**
     * 根据 NettyChannel 解析出远程服务器地址
     * @param channel netty channel
     * @return IP:PORT
     */
    public static String getAddress(Channel channel) {
        String ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) channel.remoteAddress()).getPort();
        return getAddress(ip, port);
    }

    public static String getAddress(String ip, int port) {
        return String.format("%s:%d", ip, port);
    }

}
