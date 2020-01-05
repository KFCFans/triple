package com.tjq.triple.bootstrap;

import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleRegisterType;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.common.utils.NetUtils;
import com.tjq.triple.provider.SpringProviderFactory;
import com.tjq.triple.transport.netty4.NettyServerBootstrap;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

    public void start() {
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
    private void register() {
        if (registryConfig.getRegisterType() == TripleRegisterType.DIRECT) {
            return;
        }
        // 连接注册中心，写入 server 信息
    }
    private void startService() {
        switch (TripleGlobalConfig.getProtocol()) {
            case TRIPLE_NETTY:
                NettyServerBootstrap server = new NettyServerBootstrap(NetUtils.getLocalHost(), port);
                server.start();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        SpringProviderFactory factory = new SpringProviderFactory(ctx);
        factory.init();
    }
}
