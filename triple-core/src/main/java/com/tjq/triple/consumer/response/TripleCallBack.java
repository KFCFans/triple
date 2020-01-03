package com.tjq.triple.consumer.response;

import com.tjq.triple.protocol.rpc.TripleRpcResponse;

/**
 * 回调方法
 *
 * @author tjq
 * @since 2020/1/4
 */
@FunctionalInterface
public interface TripleCallBack {

    /**
     * RPC 请求返回时调用
     * @param tripleRpcResponse RPC请求结果
     */
    void onComplete(TripleRpcResponse tripleRpcResponse);
}
