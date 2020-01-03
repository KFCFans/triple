package com.tjq.triple.transport.netty4;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * 连接池
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public class ConnectionPool {

    /**
     * IP:port -> Channel
     */
    private static final Map<String, Channel> address2Channel = Maps.newConcurrentMap();

    public static void addConnection(Channel channel) {
        String ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
        int port = ((InetSocketAddress) channel.remoteAddress()).getPort();
        String remoteAddress = genAddress(ip, port);
        address2Channel.put(remoteAddress, channel);
        log.info("[Triple] remote client(address={}) connected", remoteAddress);
    }

    public static Channel getConnection(String address) {
        return address2Channel.get(address);
    }

    public static Channel getConnection(String ip, int port) {
        return address2Channel.get(genAddress(ip, port));
    }

    /**
     * 是否有可用连接
     */
    public static boolean hasAvailableConnection() {
        if (address2Channel.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, Channel> entry : address2Channel.entrySet()) {
            if (entry.getValue().isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定 IP 是否有可用连接
     */
    public static boolean hasAvailableConnection(String address) {
        Channel channel = address2Channel.get(address);
        if (channel == null) {
            return false;
        }
        return channel.isActive();
    }

    private static String genAddress(String ip, int port) {
        return String.format("%s:%d", ip, port);
    }
}
