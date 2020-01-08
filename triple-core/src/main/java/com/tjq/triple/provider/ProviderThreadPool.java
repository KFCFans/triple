package com.tjq.triple.provider;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * provider 使用的线程池
 *
 * @author tjq
 * @since 2020/1/5
 */
@Slf4j
public final class ProviderThreadPool {

    @Getter(lazy = true)
    private final static Executor pool = initPool();

    private static Executor initPool() {
        // 默认线程池
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("triple-provider-pool-%d")
                .setUncaughtExceptionHandler((t, e) -> log.error("[Triple] execute failed", e))
                .build();
        return new ThreadPoolExecutor(
                TripleGlobalConfig.getInvokerPoolCoreSize(),
                TripleGlobalConfig.getInvokerPoolMaxSize(),
                TripleGlobalConfig.getInvokerPoolKeepAliveTimeMS(), TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(), threadFactory,
                // 拒绝执行并抛出异常
                new ThreadPoolExecutor.AbortPolicy());
    }
}
