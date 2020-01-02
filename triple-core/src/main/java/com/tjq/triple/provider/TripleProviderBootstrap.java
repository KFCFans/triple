package com.tjq.triple.provider;

import com.google.common.collect.Maps;
import com.tjq.triple.annotation.TripleProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * RPC 服务端启动器，需要完成以下事件
 * 1. 初始化注册中心，以便进行服务发现
 * 2. 初始化服务提供者（代理化 & 服务注册）
 * 3. 启动netty
 *
 * @author tjq
 * @since 2020/1/2
 */
@Slf4j
public class TripleProviderBootstrap implements ApplicationContextAware, InitializingBean {

    private Map<String, Map<String, Object>> providerData;

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * 外部 Spring 容器的上下文，根据自定义注解拿到服务提供者
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> beanName2Bean = ctx.getBeansWithAnnotation(TripleProvider.class);
        if (CollectionUtils.isEmpty(beanName2Bean)) {
            log.warn("[TripleServer] no service provider was been found.");
            return;
        }
        providerData = Maps.newHashMap();
        beanName2Bean.forEach((beanName, bean) -> {
            TripleProvider tripleProvider = bean.getClass().getAnnotation(TripleProvider.class);
            String serviceUniqueKey = genServiceKey(tripleProvider.group(), tripleProvider.version());
            Map<String, Object> ifName2PB = providerData.computeIfAbsent(serviceUniqueKey, ignore -> Maps.newHashMap());
            ifName2PB.put(genServiceInterfaceName(tripleProvider), genProxy(bean));
        });
        log.info("[TripleServer] triple finds {} provider successfully.", beanName2Bean.size());
    }

    /**
     * 生成一级索引：group + version
     */
    private static String genServiceKey(String group, String version) {
        return String.format("%s_%s", group, version);
    }

    /**
     * 生成 TripleProvider 接口名称
     */
    private static String genServiceInterfaceName(TripleProvider tripleProvider) {
        if (tripleProvider.interfaceClass() != void.class) {
            return tripleProvider.interfaceClass().getName();
        }
        if (!StringUtils.isEmpty(tripleProvider.interfaceName())) {
            return tripleProvider.interfaceName();
        }
        Class<?>[] interfaces = tripleProvider.getClass().getInterfaces();
        switch (interfaces.length) {
            case 0: return tripleProvider.getClass().getName();
            case 1:return interfaces[0].getName();
        }
        log.error("[TripleServer] init provider failed, please specify the interfaceName or interfaceClass");
        throw new RuntimeException("please specify the interfaceName or interfaceClass");
    }

    /**
     * 代理化
     */
    private static Object genProxy(Object bean) {
        return bean;
    }

}
