package com.tjq.triple.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tjq.triple.registry.ProviderAddressPool;
import com.tjq.triple.transport.AvailableProviderAddressPool;
import lombok.AllArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * description
 *
 * @author tjq
 * @since 2020/1/8
 */
@AllArgsConstructor
public class RegistrationInfoChangeListener implements PathChildrenCacheListener {

    private final String group;
    private final String serviceName;
    private final String version;

    @Override
    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        JSONObject info = JSON.parseObject(event.getData().getData(), JSONObject.class);
        String ip = info.getString("ip");
        switch (event.getType()) {
            case CHILD_ADDED:
                ProviderAddressPool.add(group, serviceName, version, ip);
                break;
            case CHILD_REMOVED:
                ProviderAddressPool.remove(group, serviceName, version, ip);
                break;
        }
    }
}
