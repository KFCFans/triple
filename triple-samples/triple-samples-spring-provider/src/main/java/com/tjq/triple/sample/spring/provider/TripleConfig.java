package com.tjq.triple.sample.spring.provider;

import com.google.common.collect.Lists;
import com.tjq.triple.bootstrap.TripleServerBootstrap;
import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleRegisterType;
import com.tjq.triple.common.enums.TripleRemoteProtocol;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务器启动配置
 *
 * @author tjq
 * @since 2020/1/5
 */
@Configuration
public class TripleConfig {

    @Bean
    public TripleServerBootstrap initTriple() {

        // 1. 全局配置
        TripleGlobalConfig.setProtocol(TripleRemoteProtocol.TRIPLE_NETTY);
        TripleGlobalConfig.setTimeoutMS(2000);

        // 2. 注册中心配置
        TripleRegistryConfig registryConfig = new TripleRegistryConfig();
        registryConfig.setRegisterType(TripleRegisterType.ZOOKEEPER);
        registryConfig.setAddressList(Lists.newArrayList("115.159.215.229:2181"));

        // 3. 创建服务器
        TripleServerBootstrap server = new TripleServerBootstrap();
        server.setPort(9876);
        server.setRegistryConfig(registryConfig);

        return server;
    }

}
