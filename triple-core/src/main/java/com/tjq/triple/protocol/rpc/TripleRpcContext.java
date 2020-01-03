package com.tjq.triple.protocol.rpc;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * RPC 调用上下文
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
public class TripleRpcContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;
    private long startTime;
    private String version;
}
