package com.tjq.triple.consumer.response;

import com.google.common.collect.Maps;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;

import java.util.Map;

/**
 * 存储等待返回结果的 RPCFuture
 *
 * @author tjq
 * @since 2020/1/3
 */
public class FuturePool {

    /**
     * requestId -> TripleFuture
     * 1. 发起 RPC 请求时，本地生成该应用唯一的ID，通过 MAP 绑定该 ID 和 Future
     * 2. 收到 Response 请求后，根据 ID 取出 Future，将结果注入
     * 3. 同步则 get() 返回，异步则调用接口方法完成回调
     * 4. 将 Future 从该 Map 中删除
     *
     * additional. 定时轮询该 Map，删除超长时间未删除的 Future，防止内存泄漏
     */
    private static final Map<Long, TripleFuture> STORE = Maps.newConcurrentMap();

    public static TripleFuture push(TripleRpcRequest request) {
        Long requestId = request.getContext().getRequestId();
        TripleFuture future = new TripleFuture(request);
        STORE.put(requestId, future);
        return future;
    }

    public static void remove(Long id) {
        STORE.remove(id);
    }

    public static TripleFuture get(Long id) {
        return STORE.get(id);
    }
}
