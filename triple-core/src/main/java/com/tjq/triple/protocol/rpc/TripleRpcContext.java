package com.tjq.triple.protocol.rpc;

import com.google.common.collect.Maps;
import com.tjq.triple.common.UniqueIDGenerator;
import com.tjq.triple.protocol.TripleTransportObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * RPC 调用上下文
 *
 * @author tjq
 * @since 2020/1/3
 */
@Setter
@Getter
@ToString
public class TripleRpcContext implements TripleTransportObject {

    private static final long serialVersionUID = 1L;

    /**
     * 本地唯一ID，Triple 负责生成
     */
    private Long requestId;
    /**
     * 分布式唯一ID，调用方若有链路追踪的需求则自行传入
     */
    private String traceId;
    /**
     * 该请求的重试次数
     */
    private Integer retryTimes;
    private Long startTime;
    private String version;

    private Map<String, Object> context;

    public TripleRpcContext() {
        requestId = UniqueIDGenerator.allocate();
        startTime = System.currentTimeMillis();
        version = "triple:1.0.0";
        retryTimes = 0;
    }

    public void addContext(String key, Object value) {
        if (context == null) {
            context = Maps.newHashMap();
        }
        context.put(key, value);
    }
}
