package com.tjq.triple.protocol;

import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * command
 *
 * @author tjq
 * @since 2020/1/3
 */
@Getter
public enum TripleRpcCMD {

    RPC_REQUEST(1, TripleRpcRequest.class),
    RPC_RESPONSE(2, TripleRpcResponse.class);

    private byte cmd;
    private Class<? extends TripleTransportObject> clazz;

    TripleRpcCMD(int v, Class<? extends TripleTransportObject> clazz) {
        this.cmd = (byte) v;
        this.clazz = clazz;
    }

    public static TripleRpcCMD of(byte v) {
        switch (v) {
            case 1: return RPC_REQUEST;
            case 2: return RPC_RESPONSE;
        }
        throw new TripleRpcException("out of enum definition");
    }

}
