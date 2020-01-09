package com.tjq.triple.transport;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tjq.triple.registry.ProviderAddressPool;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 存储当前可用的服务提供者的地址，维护 service uniqueKey -> provider host list 的映射关系
 *
 * @author tjq
 * @since 2020/1/9
 */
public class AvailableProviderAddressPool {

    public static final Set<String> USING_HOST_SET = Sets.newConcurrentHashSet();
    public static final Map<String, List<String>> SERVICE2HOSTS = Maps.newConcurrentMap();

    public static String getOneHost(String group, String serviceName, String version) {
        String serviceKey = ProviderAddressPool.genKey(group, serviceName, version);
        List<String> list = SERVICE2HOSTS.get(serviceKey);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        int size = list.size();
        if (size == 1) {
            return list.get(0);
        }
        return list.get(ThreadLocalRandom.current().nextInt(size));
    }
}
