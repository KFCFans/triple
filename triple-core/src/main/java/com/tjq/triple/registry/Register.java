package com.tjq.triple.registry;

import java.util.List;

/**
 * 服务注册与发现
 *
 * @author tjq
 * @since 2020/1/7
 */
public interface Register {

    /**
     * 服务注册
     */
    void register(List<ProviderBeanInfo> beans) throws Exception;

    /**
     * 服务发现
     */
    void discover(List<ProviderBeanInfo> beans) throws Exception;
}
