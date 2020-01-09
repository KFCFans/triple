package com.tjq.triple.bootstrap;

import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleRegisterType;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.common.utils.NetUtils;
import com.tjq.triple.provider.ProviderFactory;
import com.tjq.triple.provider.SpringProviderFactory;
import com.tjq.triple.registry.ProviderBeanInfo;
import com.tjq.triple.registry.zookeeper.ZKRegister;
import com.tjq.triple.transport.netty4.server.NettyServer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * triple rpc server 启动器
 *
 * @author tjq
 * @since 2020/1/5
 */
@Slf4j
@Setter
public class TripleServerBootstrap implements InitializingBean, ApplicationContextAware {

    private Integer port;
    private TripleRegistryConfig registryConfig;

    public void start() throws Exception {
        long s = System.currentTimeMillis();
        check();
        register();
        startService();
        long usedTime = System.currentTimeMillis() - s;
        log.info("[Triple] server start up successfully, using {} ms.", usedTime);
    }

    private void check() {
        if (registryConfig == null) {
            throw new TripleRpcException("please set register info");
        }
        if (port == null) {
            throw new TripleRpcException("please set the port");
        }
    }
    private void register() throws Exception {
        if (registryConfig.getRegisterType() == TripleRegisterType.DIRECT) {
            return;
        }
        // 生成 provider bean 信息
        List<ProviderBeanInfo> providerBeanInfos = ProviderFactory.getProviderBeanInfos();

        // 连接注册中心，写入 server 信息
        if (registryConfig.getRegisterType() == TripleRegisterType.ZOOKEEPER) {
            ZKRegister zkRegister = new ZKRegister(registryConfig.getAddressList());
            zkRegister.register(providerBeanInfos);
        }
    }
    private void startService() {
        switch (TripleGlobalConfig.getProtocol()) {
            case TRIPLE_NETTY:
                NettyServer server = new NettyServer(NetUtils.getLocalHost(), port);
                server.start();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext ctx) throws BeansException {
        SpringProviderFactory factory = new SpringProviderFactory(ctx);
        factory.init();
    }
}
