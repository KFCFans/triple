package com.tjq.triple.transport;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 传输器池
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
     * 添加传输器
     */
    public static void addTransporter(Transporter transporter) {
        String remoteAddress = transporter.remoteAddress();
        address2Transporter.put(remoteAddress, transporter);
        log.info("[Triple] remote client(address={}) connected", remoteAddress);
    }

    public static Transporter getTransporter(String address) {
        return address2Transporter.get(address);
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
