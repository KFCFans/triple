package com.tjq.triple.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 本地唯一ID生成器
 *
 * @author tjq
 * @since 2020/1/3
 */
public class UniqueIDGenerator {

    // 0 -> MAX -> MIN -> 0 loop
    private static final AtomicLong ID_FACTORY = new AtomicLong(0);

    public static Long allocate() {
        return ID_FACTORY.getAndIncrement();
    }
}
