package com.tjq.triple.registry.zookeeper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.tjq.triple.common.CommonSJ;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.common.utils.CommonUtils;
import com.tjq.triple.common.utils.NetUtils;
import com.tjq.triple.registry.ProviderAddressPool;
import com.tjq.triple.registry.ProviderBeanInfo;
import com.tjq.triple.registry.Register;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * description
 *
 * @author tjq
 * @since 2020/1/7
 */
@Slf4j
public class ZKRegister implements Register {

    private final CuratorFramework curator;

    public ZKRegister(List<String> zkClusterIps) {
        String zkAddress = CommonSJ.COMMA_JOINER.join(zkClusterIps);
        RetryPolicy retryPolicy = new RetryUntilElapsed(5, 200);
        curator = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .retryPolicy(retryPolicy)
                .namespace("triple")
                .build();
        curator.start();
        log.info("[Triple] curator(zookeeper client) started successfully.");
    }


    @Override
    public void register(List<ProviderBeanInfo> beans) throws Exception {

        Stopwatch sw = Stopwatch.createStarted();
        String ip = NetUtils.getLocalHost();
        for (ProviderBeanInfo bean : beans) {
            Supplier<Void> executor = () -> {
                String group = bean.getGroup();
                String serviceName = bean.getServiceName();
                String version = bean.getVersion();
                bean.addInfo("ip", ip);
                String path = String.format("/%s/%s/%s/provider/%s", group, serviceName, version, ip);
                try {
                    curator.create()
                            .orSetData()
                            .creatingParentsIfNeeded()
                            .withMode(CreateMode.EPHEMERAL)
                            .forPath(path, JSON.toJSONBytes(bean.getInfo()));

                    log.info("[Triple] service({}#{}:{}) registered success.", group, serviceName, version);
                } catch (Exception e) {
                    throw new TripleRpcException(e);
                }
                return null;
            };
            CommonUtils.executeWithRetry(executor, 3, 100);
        }
        sw.stop();
        log.info("[Triple] all service registration by zookeeper successful, using {}ms.", sw.elapsed(TimeUnit.MILLISECONDS));
    }

    @Override
    public void discover(List<ProviderBeanInfo> beans) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        for (ProviderBeanInfo bean : beans) {
            String group = bean.getGroup();
            String serviceName = bean.getServiceName();
            String version = bean.getVersion();
            String path = String.format("/%s/%s/%s/provider", group, serviceName, version);
            List<String> providerIps = curator.getChildren().forPath(path);
            ProviderAddressPool.add(group, serviceName, version, providerIps);
            log.info("[Triple] {}#{}:{}'s provider ip list:{}", group, serviceName, version, providerIps);

            // 注册监听
            PathChildrenCache cache = new PathChildrenCache(curator, path, true);
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            cache.getListenable().addListener(((client, event) -> {

            }));

        }
        sw.stop();
        log.info("[Triple] service discovery by zookeeper successful, using {}ms.", sw.elapsed(TimeUnit.MILLISECONDS));

    }
}
