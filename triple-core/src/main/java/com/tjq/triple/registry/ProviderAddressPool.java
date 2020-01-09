package com.tjq.triple.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 存储所有服务提供者的地址
 *
 * @author tjq
 * @since 2020/1/7
 */
@Slf4j
public class ProviderAddressPool {

    /**
     * group -> serviceName -> version -> providerIPList
     */
    public static final Map<String, Set<String>> POOL = Maps.newConcurrentMap();


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

    }

    public static Set<String> getAllProviderHosts(String serviceKey) {
        return POOL.computeIfAbsent(serviceKey, ignore -> Sets.newHashSet());
    }

    public static String genKey(String group, String serviceName, String version) {
        return String.format("%s#%s:%s", group, serviceName, version);
    }
}
