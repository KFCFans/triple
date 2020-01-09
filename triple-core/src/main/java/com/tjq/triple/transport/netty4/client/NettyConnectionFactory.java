package com.tjq.triple.transport.netty4.client;

import com.google.common.collect.Lists;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.registry.ProviderAddressPool;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.tjq.triple.transport.AvailableProviderAddressPool.*;

/**
 * RPC consumer 连接 provider 的初始化连接建立器
 *
 * @author tjq
 * @since 2020/1/8
 */
@Slf4j
public class NettyConnectionFactory {

    // 持有 Netty 客户端
    @Getter(lazy = true)
    private static final NettyClient client = new NettyClient();

    /**
     * 建立（重建）连接池，遍历入参Map，进行以下操作
     * 1. 如果当前没有IP，则随机取出一个 providerIP 进行连接
     * 2. 如果当前 service 的IP池中包含当前已经建立连接的IP，则跳过对该 service 的初始化
     * 3. 否则初始化该 service 对应的某一个IP
     */
    public static synchronized void rebuild() {
        ProviderAddressPool.POOL.forEach((serviceKey, hostSet) -> {

            // 初始化情况，任选一个 IP 连接
            if (USING_HOST_SET.isEmpty()) {
                String host = connectOneHost(hostSet);
                USING_HOST_SET.add(host);
                return;
            }

            // rebuild 专用，判断该IP是否已经初始化
            List<String> currentHosts = SERVICE2HOSTS.get(serviceKey);
            if (!CollectionUtils.isEmpty(currentHosts)) {
                return;
            }

            // 判断现有IP是否可调用下一个服务
            for (String usingHost : USING_HOST_SET) {
                if (hostSet.contains(usingHost)) {
                    return;
                }
            }

            // 当前服务没有任何可用IP连接，开始初始化
            USING_HOST_SET.add(connectOneHost(hostSet));
        });

        // 构建 service -> 可用IP池
        ProviderAddressPool.POOL.forEach((serviceKey, allProviderHosts) -> {
            List<String> hosts = SERVICE2HOSTS.computeIfAbsent(serviceKey, ignore -> Lists.newArrayList());
            allProviderHosts.forEach(ph -> {
                if (USING_HOST_SET.contains(ph)) {
                    hosts.add(ph);
                }
            });
        });
    }

    /**
     * 删除某些宕机的 provider IPs，并重建可用连接池
     * @param address 宕机/不活跃/超出执行能力的 provider IPs
     */
    public static synchronized void loseProviders(String address) {
        // 该 provider 并未被使用，无关痛痒
        if (!USING_HOST_SET.remove(address)) {
            return;
        }
        SERVICE2HOSTS.forEach((serviceKey, hosts) -> hosts.remove(address));
        rebuild();
    }


    /**
     * 建立一个新的连接
     */
    public static synchronized String connectOneHost(Set<String> hostSet) {
        List<String> hostList = Lists.newArrayList(hostSet);
        // 随机打散
        int size = hostList.size();
        for (int i = 0; i < 5; i++) {
            int s = ThreadLocalRandom.current().nextInt(size);
            int e = size - s - 1;
            String tmp = hostList.get(s);
            hostList.set(s, hostList.get(e));
            hostList.set(e, tmp);
        }
        // 开始连接，直到第一个成功
        for (String host : hostList) {

            if (USING_HOST_SET.contains(host)) {
                continue;
            }
            String[] split = host.split(":");
            try {
                getClient().connect(split[0], Integer.parseInt(split[1]));
                log.info("[Triple] connect to remote server({}) success", host);
                USING_HOST_SET.add(host);
                return host;
            }catch (Exception e) {
                log.warn("[Triple] connect to remote address({}) failed.", host, e);
            }
        }
        // 尝试所有IP，连接失败
        log.error("[Triple] connect to all address({}) failed.", hostSet);
        throw new TripleRpcException("connect to server failed");
    }
}
