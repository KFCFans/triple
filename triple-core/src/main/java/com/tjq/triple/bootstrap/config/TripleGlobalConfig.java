package com.tjq.triple.bootstrap.config;

import com.tjq.triple.common.enums.TripleRemoteProtocol;
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
public final class TripleGlobalConfig {

    /**
     * 序列化方式
     */
    @Setter
    @Getter
    private static TripleSerializerType serializerType = TripleSerializerType.KRYO;
    /**
     * 通讯方式
     */
    @Setter
    @Getter
    private static TripleRemoteProtocol protocol = TripleRemoteProtocol.TRIPLE_NETTY;
    /**
     * RPC 超时时间
     */
    @Setter
    @Getter
    private static long timeoutMS = 2000;
    /**
     * RPC 调用失败重试次数
     */
    @Setter
    @Getter
    private static int retryTimes = 3;
    /**
     * 本地服务执行的线程池（可选，默认配置为 CPU核心数*2, 200, 60S）
     */
    @Setter
    @Getter
    private static int invokerPoolCoreSize = Runtime.getRuntime().availableProcessors() * 2;
    @Setter
    @Getter
    private static int invokerPoolMaxSize = 200;
    @Setter
    @Getter
    private static int invokerPoolKeepAliveTimeMS = 60000;


}
