package com.tjq.triple.consumer.response;

import com.google.common.collect.Lists;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Triple RPC 调用后的 Future
 *
 * @author tjq
 * @since 2020/1/3
 */
public class TripleFuture implements Future<TripleRpcResponse> {

    @Getter
    private final long createdTime;

    // 判断是否完成
    private final CountDownLatch latch;
    // 便于日志记录
    private final TripleRpcRequest request;
    // 存储返回数据
    private TripleRpcResponse response;
    // 回调
    private List<TripleCallBack> callBacks;

    public TripleFuture(TripleRpcRequest tripleRpcRequest) {
        createdTime = System.currentTimeMillis();
        latch = new CountDownLatch(1);
        request = tripleRpcRequest;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return latch.getCount() == 0;
    }

    @Override
    public TripleRpcResponse get() throws InterruptedException, ExecutionException {
        latch.await();
        return response;
    }

    @Override
    public TripleRpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean done = latch.await(timeout, unit);
        if (done) {
            return response;
        }
        String s = String.format("triple rpc invoke timeout(class=%s&method=%s)", request.getClassName(), request.getMethodName());
        throw new TripleRpcException(s);
    }

    /**
     * 设置返回结果
     */
    public void finishedInvoke(TripleRpcResponse response) {
        this.response = response;
        latch.countDown();
        // 执行回调
        if (callBacks != null) {
            callBacks.forEach(c -> c.onComplete(response));
        }
    }

    /**
     * 添加回调监听
     */
    public void addListeners(TripleCallBack... cbs) {
        if (callBacks == null) {
            callBacks = Lists.newLinkedList();
        }
        callBacks.addAll(Arrays.asList(cbs));
    }
}
