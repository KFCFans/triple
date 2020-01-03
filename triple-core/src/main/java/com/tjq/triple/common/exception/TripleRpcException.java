package com.tjq.triple.common.exception;

/**
 * 自定义异常
 *
 * @author tjq
 * @since 2020/1/3
 */
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
