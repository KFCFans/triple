package com.tjq.triple.transport;

import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.TripleRpcCMD;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;

/**
 * 连接器
 *
 * @author tjq
 * @since 2020/1/3
 */
public interface Transporter {

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
        response.setMessage("transport data failed");

        FuturePool.get(response.getRequestId()).finishedInvoke(response);
    }
}
