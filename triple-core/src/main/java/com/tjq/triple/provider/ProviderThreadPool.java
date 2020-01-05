package com.tjq.triple.provider;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
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

    private static volatile Executor pool;

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_SIZE = 200;
    private static final int KEEP_ALIVE_TIME_S = 60;

    public static Executor getPool() {
        if (pool == null) {
            synchronized (ProviderThreadPool.class) {
                if (pool == null) {

                    if (TripleGlobalConfig.getLocalInvokerPool() != null) {
                        pool = TripleGlobalConfig.getLocalInvokerPool();
                    }else {
                        // 默认线程池
                        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                                .setNameFormat("triple-provider-pool-%d")
                                .setUncaughtExceptionHandler((t, e) -> log.error("[Triple] execute failed", e))
                                .build();
                        pool = new ThreadPoolExecutor(
                                CORE_SIZE,
                                MAX_SIZE,
                                KEEP_ALIVE_TIME_S, TimeUnit.SECONDS,
                                new SynchronousQueue<>(), threadFactory,
                                // 拒绝执行并抛出异常
                                new ThreadPoolExecutor.AbortPolicy());
                    }

                }
            }
        }
        return pool;
    }
}
