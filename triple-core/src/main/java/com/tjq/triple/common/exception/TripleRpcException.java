package com.tjq.triple.common.exception;

import lombok.NoArgsConstructor;

/**
 * RPC 调用产生的异常
 *
 * @author tjq
 * @since 2020/1/3
 */
@NoArgsConstructor
public class TripleRpcException extends RuntimeException {

    public TripleRpcException(String msg) {
        super(msg);
    }

    public TripleRpcException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TripleRpcException(Throwable cause) {
        super(cause);
    }
}
