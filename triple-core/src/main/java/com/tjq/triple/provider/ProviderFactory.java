package com.tjq.triple.provider;

import com.google.common.collect.Maps;
import com.tjq.triple.annotation.TripleProvider;
import com.tjq.triple.provider.invoker.JDKReflectInvoker;
import com.tjq.triple.provider.invoker.LocalInvoker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 初始化 provider
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public abstract class ProviderFactory {

    // group_version -> (className -> invoker)
    private final Map<String, Map<String, LocalInvoker>> providerData;

    protected abstract Collection<Object> providerBeans();

    public ProviderFactory() {

        providerData = Maps.newHashMap();

        Collection<Object> beans = providerBeans();

        if (CollectionUtils.isEmpty(beans)) {
            log.warn("[TripleProvider] no service provider was been found.");
            return;
        }
        beans.forEach(bean -> {
            TripleProvider tripleProvider = bean.getClass().getAnnotation(TripleProvider.class);
            String serviceUniqueKey = genServiceKey(tripleProvider.group(), tripleProvider.version());
            Map<String, LocalInvoker> ifName2PB = providerData.computeIfAbsent(serviceUniqueKey, ignore -> Maps.newHashMap());
            ifName2PB.put(genServiceInterfaceName(tripleProvider), genProxy(bean));
        });
        log.info("[TripleProvider] triple finds {} provider successfully.", beans.size());
    }

    public Object invoke(String group, String version, String className, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
        Map<String, LocalInvoker> className2Invoker = providerData.get(genServiceKey(group, version));
        if (CollectionUtils.isEmpty(className2Invoker)) {
            log.error("[TripleProvider] no provider found by group:{} and version:{}", group, version);
            throw new RuntimeException("no provider found by current group and version");
        }
        LocalInvoker invoker = className2Invoker.get(className);
        if (invoker == null) {
            log.error("[TripleProvider] no provider found by className:{}", className);
            throw new RuntimeException("no provider found by className:" + className);
        }
        return invoker.invoke(methodName, parameterTypes, parameters);
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
        log.error("[TripleProvider] init provider failed, please specify the interfaceName or interfaceClass");
        throw new RuntimeException("please specify the interfaceName or interfaceClass");
    }

    /**
     * 代理化，后期根据外部配置选择代理调用方式（CGlib可能存在jar包冲突问题）
     */
    private static LocalInvoker genProxy(Object bean) {
        return new JDKReflectInvoker(bean);
    }

}
