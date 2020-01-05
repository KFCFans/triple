package com.tjq.triple.bootstrap.config;

import com.tjq.triple.common.enums.TripleSerializerType;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executor;

/**
 * 全局配置
 * 注册中心模式下，Client的配置会被Server覆盖
 * 直连模式下，请务必保持参数一直
 *
 * @author tjq
 * @since 2020/1/5
 */
@Setter
public final class TripleGlobalConfig {

    /**
     * 序列化方式
     */
    @Getter
    private static TripleSerializerType serializerType;

    /**
     * 本地服务执行的线程池（可选，默认配置为 CPU核心数*2, 200, 60S）
     */
    @Getter
    private static Executor localInvokerPool;

}
