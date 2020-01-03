package com.tjq.triple.protocol.rpc;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * RPC 请求对象
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
public class TripleRpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String className;
    private String methodName;
    private Class<?> parameterTypes;
    private Object[] parameters;

    private TripleRpcContext context;
}
