package com.tjq.triple.transport;

import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.consumer.response.TripleFuture;
import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.TripleRpcCMD;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;

/**
 * 传输层，负责发送数据
 *
 * @author tjq
 * @since 2020/1/3
 */
public interface Transporter {

    /**
     * 远程地址
     */
    String remoteAddress();

    /**
     * 连接器是否处于可用状态
     */
    boolean available();

    void sendSync(TripleProtocol protocol, long timeoutMS) throws TripleRpcException;

    void sendAsync(TripleProtocol protocol);

    default void processFailed(TripleProtocol protocol) {
        TripleRpcCMD cmd = TripleRpcCMD.of(protocol.getCmd());
        if (cmd == TripleRpcCMD.RPC_RESPONSE) {
            return;
        }
        // 返回请求失败
        TripleRpcResponse response = new TripleRpcResponse();
        response.setCode(TripleRpcResponse.INVOKE_FAILED);
        response.setRequestId(((TripleRpcRequest)protocol.getData()).getContext().getRequestId());
        response.setThrowable(new TripleRpcException("transport data failed"));

        TripleFuture future = FuturePool.get(response.getRequestId());
        // 不一定存在，比如RPC调用超时设为10MS，传输用了20MS，然后失败
        if (future != null) {
            future.finishedInvoke(response);
        }
    }
}
