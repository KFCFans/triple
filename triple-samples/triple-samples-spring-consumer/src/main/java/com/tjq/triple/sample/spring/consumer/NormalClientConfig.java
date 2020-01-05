package com.tjq.triple.sample.spring.consumer;

import com.google.common.collect.Lists;
import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleRegisterType;
import com.tjq.triple.common.enums.TripleRemoteProtocol;
import com.tjq.triple.common.enums.TripleSerializerType;
import com.tjq.triple.consumer.TripleReferenceBean;
import com.tjq.triple.sample.api.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * use referenceBean
 *
 * @author tjq
 * @since 2020/1/5
 */
@Configuration
public class NormalClientConfig {

    @Bean
    public HelloService initTripleConsumer() throws Exception {

        // 1. 全局设置
        TripleGlobalConfig.setProtocol(TripleRemoteProtocol.TRIPLE_NETTY);
        TripleGlobalConfig.setTimeoutMS(2000);
        TripleGlobalConfig.setSerializerType(TripleSerializerType.KRYO);

        // 2. 注册中心设置
        TripleRegistryConfig registryConfig = new TripleRegistryConfig();
        registryConfig.setRegisterType(TripleRegisterType.DIRECT);
        registryConfig.setAddressList(Lists.newArrayList("192.168.124.9:9876"));

        // 3. 配置 BEAN
        TripleReferenceBean<HelloService> rfBean = new TripleReferenceBean<>();
        rfBean.setRegistryConfig(registryConfig);
        rfBean.setInterfaceClass(HelloService.class);


        return rfBean.get();
    }

}
