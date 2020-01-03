package com.tjq.triple.protocol.rpc;

import com.tjq.triple.protocol.TripleTransportObject;
import lombok.Getter;
import lombok.Setter;

/**
 * RPC 响应对象
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
public class TripleRpcResponse implements TripleTransportObject {

    private static final long serialVersionUID = 1L;

    private short code;
    private String requestId;
    private Object result;
    private String message;

    /* ********* code definition ********* */

    // 2XX：调用成功且正常返回
    public static final short SUCCESS = 200;
    public static final short INVOKE_SUCCESS_EXECUTE_FAILED = 201;
    // 4XX：调用失败，比如网络原因导致TCP连接断开等
    public static final short INVOKE_FAILED = 400;
}
