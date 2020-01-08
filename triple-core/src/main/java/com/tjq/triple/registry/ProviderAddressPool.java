package com.tjq.triple.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tjq.triple.common.exception.TripleRpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 存储服务提供者的地址
 *
 * @author tjq
 * @since 2020/1/7
 */
@Slf4j
public class ProviderAddressPool {

    /**
     * group -> serviceName -> version -> providerIPList
     */
    private static final Map<String, Set<String>> POOL = Maps.newConcurrentMap();
    private static final Map<String, String> CURRENT = Maps.newConcurrentMap();

    public static void add(String group, String serviceName, String version, String... address) {
        add(group, serviceName, version, Lists.newArrayList(address));
    }

    public static void add(String group, String serviceName, String version, List<String> address) {
        String key = genKey(group, serviceName, version);
        Set<String> ips = POOL.computeIfAbsent(key, ignore -> Sets.newConcurrentHashSet());
        ips.addAll(address);
    }

    /**
     * 同步移除 providerIP
     */
    public static synchronized void remove(String group, String serviceName, String version, String address) {
        String key = genKey(group, serviceName, version);
        try {
            POOL.get(key).remove(address);
        }catch (Exception ignore) {
        }
        String invalidIP = CURRENT.get(key);
        if (invalidIP == null) {
            return;
        }
        // 该 provider 下线，需要重新建立所有相关的连接
        List<String> affectedService = Lists.newArrayList();
        CURRENT.forEach((k, ip) -> {
            if (invalidIP.equals(ip)) {
                affectedService.add(k);
            }
        });
        affectedService.forEach(serviceKey -> {

            List<String> ips = Lists.newArrayList(POOL.get(serviceKey));
            String nextIp = ips.get(ThreadLocalRandom.current().nextInt(ips.size()));

        });
    }

    private static String genKey(String group, String serviceName, String version) {
        return String.format("%s#%s:%s", group, serviceName, version);
    }
}
