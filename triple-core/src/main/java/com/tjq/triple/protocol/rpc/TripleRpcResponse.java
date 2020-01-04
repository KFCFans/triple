package com.tjq.triple.protocol.rpc;

import com.tjq.triple.protocol.TripleTransportObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * RPC 响应对象
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class TripleRpcResponse implements TripleTransportObject {

    private static final long serialVersionUID = 1L;

    private short code;
    private Long requestId;
    private String traceId;
    private Object result;
    private Throwable throwable;

    /* ********* code definition ********* */

    // 2XX：调用成功且正常返回
    public static final short SUCCESS = 200;
    public static final short INVOKE_SUCCESS_EXECUTE_FAILED = 201;
    // 4XX：调用失败，比如网络原因导致TCP连接断开等
    public static final short INVOKE_FAILED = 400;
}
