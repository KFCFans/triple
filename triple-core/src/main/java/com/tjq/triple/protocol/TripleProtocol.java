package com.tjq.triple.protocol;

import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 自定义 RPC 协议
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
@NoArgsConstructor
public class TripleProtocol implements Serializable {

    public static final short TRIPLE_PROTOCOL_MAGIC = 10086;

    private short magic = TRIPLE_PROTOCOL_MAGIC;
    private byte extension;
    private byte cmd;

    private int length;

    private TripleTransportObject data;

    public static TripleProtocol buildRpcRequest(TripleRpcRequest rpcRequest) {
        TripleProtocol protocol = new TripleProtocol();
        protocol.extension = 0;
        protocol.cmd = TripleRpcCMD.RPC_REQUEST.getCmd();
        protocol.data = rpcRequest;
        return protocol;
    }

    public static TripleProtocol buildRpcResponse(TripleRpcResponse rpcResponse) {
        TripleProtocol protocol = new TripleProtocol();
        protocol.extension = 0;
        protocol.cmd = TripleRpcCMD.RPC_RESPONSE.getCmd();
        protocol.data = rpcResponse;
        return protocol;
    }
}
