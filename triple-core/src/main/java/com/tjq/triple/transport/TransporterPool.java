package com.tjq.triple.transport;

import com.google.common.collect.Maps;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleRegisterType;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.transport.netty4.NettyTransporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 传输层公共连接池，维护 host(ip:port) -> data channel 的一对一映射关系
 *
 * @author tjq
 * @since 2020/1/4
 */
@Slf4j
public class TransporterPool {

    /**
     * address(ip:port) -> Transporter
     */
    private static final Map<String, Transporter> address2Transporter = Maps.newConcurrentMap();

    /**
     * 添加传输器，由 Netty channelActive 回调方法自动添加
     */
    public static void addTransporter(Transporter transporter) {
        String remoteAddress = transporter.remoteAddress();
        address2Transporter.put(remoteAddress, transporter);
        log.info("[Triple] remote client(address={}) connected", remoteAddress);
    }

    public static void removeTransporter(String remoteAddress) {
        Transporter tsp = address2Transporter.get(remoteAddress);
        if (tsp == null) {
            return;
        }
        address2Transporter.remove(remoteAddress);
        if (!tsp.available()) {
            log.warn("[Triple] remote client(address={}) disconnected because of channel inactive", remoteAddress);
        }else {
            log.info("[Triple] remove remote client(address={})", remoteAddress);
        }
    }

    public static Transporter getTransporter(String address) {
        return address2Transporter.get(address);
    }

    public static Transporter getTransporter() {
        for (Transporter tsp : address2Transporter.values()) {
            if (tsp.available()) {
                return tsp;
            }
        }
        return null;
    }

    /**
     * 指定远程地址是否存在连接器（适用于指定 IP 调用的情况）
     */
    public static boolean hasAvailableTransporter(String address) {
        Transporter transporter = address2Transporter.get(address);
        if (transporter == null) {
            return false;
        }
        return transporter.available();
    }

    /**
     * 当前是否存在可用连接器（正常情况下，只要有一个长连接可用就行）
     * 注：目前没有考虑Provider提供服务不相同的情况（如 A 提供服务1和2，B 提供服务3和4）
     */
    public static boolean hasAvailableTransporter() {
        for (Map.Entry<String, Transporter> entry : address2Transporter.entrySet()) {
            if (entry.getValue().available()) {
                return true;
            }
        }
        return false;
    }
}
