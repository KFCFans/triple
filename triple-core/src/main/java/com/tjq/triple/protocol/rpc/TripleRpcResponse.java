package com.tjq.triple.protocol.rpc;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * RPC 响应对象
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
public class TripleRpcResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private short code;
    private String requestId;
    private Object result;
    private String message;
}
