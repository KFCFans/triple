package com.tjq.triple.protocol.rpc;

import com.tjq.triple.protocol.TripleTransportObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * RPC 请求对象
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
@NoArgsConstructor
public class TripleRpcRequest implements TripleTransportObject {

    private static final long serialVersionUID = 1L;

    private String group;
    private String version;

    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    private TripleRpcContext context;
}
