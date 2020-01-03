package com.tjq.triple.consumer;

import com.tjq.triple.transport.netty4.NettyConnectionPool;
import org.springframework.util.StringUtils;

/**
 * 远程调用初始化 Bean
 *
 * @author tjq
 * @since 2020/1/3
 */
public class TripleReferenceBean<T> {

    private transient volatile T ref;
    private transient volatile boolean initialized;

    private long timeout;

    private String groupName;
    private String version;
    private Class<?> interfaceClass;
    private String address;

    public Object get() {
        return null;
    }

    private void initConnection() {

        if (StringUtils.isEmpty(address)) {
            if (NettyConnectionPool.hasAvailableConnection()) {
                return;
            }
        }else {
            if (NettyConnectionPool.hasAvailableConnection(address)) {
                return;
            }
        }

        // 初始化一个连接
    }
}
