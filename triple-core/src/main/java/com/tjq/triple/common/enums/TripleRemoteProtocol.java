package com.tjq.triple.common.enums;

/**
 * 通讯协议选择
 *
 * @author tjq
 * @since 2020/1/4
 */
public enum TripleRemoteProtocol {

    /**
     * triple协议 + 基于netty的 TCP长连接 进行通讯
     */
    TRIPLE_NETTY,
    /**
     * triple协议 + HTTP （待实现）
     */
    TRIPLE_HTTP,
}
