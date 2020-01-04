package com.tjq.triple.consumer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleInvokeType;
import com.tjq.triple.common.enums.TripleRemoteProtocol;
import com.tjq.triple.common.enums.TripleSerializerType;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.consumer.response.TripleFuture;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import com.tjq.triple.serialize.Serializer;
import com.tjq.triple.transport.Transporter;
import com.tjq.triple.transport.TransporterPool;
import com.tjq.triple.transport.netty4.NettyClientBootstrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.*;

/**
 * 远程调用初始化 Bean
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public class TripleReferenceBean<T> {

    private transient volatile T ref;
    private transient volatile boolean initialized;

    // 注册中心配置
    private TripleRegistryConfig registryConfig;

    private long timeout;
    private String groupName;
    private String version;
    private Class<?> interfaceClass;
    private String interfaceName;
    // 同步/异步
    private TripleInvokeType invokeType;
    // 通讯方式
    private TripleRemoteProtocol protocol;
    // 序列化方式
    private TripleSerializerType serializerType;

    // provider 线程池
    private Executor providerPool;

    public TripleReferenceBean() {
        groupName = "Triple";
        version = "1.0.0";
        invokeType = TripleInvokeType.SYNC;
        protocol = TripleRemoteProtocol.TRIPLE_NETTY;
        serializerType = TripleSerializerType.KRYO;

        // 默认线程池
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("triple-pool-%d")
                .setUncaughtExceptionHandler((t, e) -> log.error("[Triple] execute failed", e))
                .build();
        providerPool = new ThreadPoolExecutor(
                8,
                200,
                60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    public Object get() throws Exception{
        // 初始化连接
        initConnection();
        // 生成代理对象
        return genProxyObject();
    }

    private void initConnection() {

        // 1. 计算需要连接的远程服务器地址
        List<String> needConnectionAddress = Lists.newLinkedList();
        switch (registryConfig.getRegisterType()) {
            case ZOOKEEPER:
                // 注册中心模式下，只要有可用连接，就无需再次注册
                if (TransporterPool.hasAvailableTransporter()) {
                    return;
                }
                // TODO：初始化连接
                return;
            case DIRECT:
                registryConfig.getAddressList().forEach(ads -> {
                    Transporter transporter = TransporterPool.getTransporter(ads);
                    if (transporter == null || !transporter.available()) {
                        needConnectionAddress.add(ads);
                    }
                });

        }

        // 2. 初始化序列化组件
        Serializer serializer = serializerType.getInstance();

        // 建立连接
        needConnectionAddress.forEach(targetAddress -> {
            String[] split = targetAddress.split(":");
            String ip = split[0];
            int port = Integer.parseInt(split[1]);
            switch (protocol) {
                case TRIPLE_NETTY:
                    NettyClientBootstrap nettyClient = new NettyClientBootstrap(ip, port, serializer, providerPool);
                    nettyClient.start();
            }
        });
    }

    /**
     * 生成代理对象
     */
    private Object genProxyObject() throws Exception {
        Class<?> ifClz;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (interfaceClass != null) {
            ifClz = interfaceClass;
        }else if (!StringUtils.isEmpty(interfaceName)) {
            ifClz = Class.forName(interfaceName);
        }else {
            throw new IllegalArgumentException("triple can't found interfaceName");
        }

        /*
            创建代理对象，对返回的 Object 的任何操作，都会被转发到下述代码执行
              * proxy:代理类对象
              * method:被调用的被代理类方法
              * args：被调用的被代理类方法的入参
              * 返回值（Object）：调用的返回值
         */
        return Proxy.newProxyInstance(loader, new Class[] {ifClz}, ((proxy, method, args) -> {

            // 1. 构造 RPC 请求
            TripleRpcRequest rpcRequest = new TripleRpcRequest();
            rpcRequest.setGroup(groupName);
            rpcRequest.setVersion(version);
            rpcRequest.setClassName(ifClz.getName());
            rpcRequest.setMethodName(method.getName());
            rpcRequest.setParameterTypes(method.getParameterTypes());
            rpcRequest.setParameters(args);

            // 2. 构造异步响应结果
            TripleFuture tripleFuture = FuturePool.push(rpcRequest);

            // 3. 发送 RPC 网络请求

            // 4. 处理返回结果
            switch (invokeType) {
                case SYNC:
                    // 超时会抛出异常，继续向外抛
                    TripleRpcResponse response = tripleFuture.get(timeout, TimeUnit.MILLISECONDS);
                    if (TripleRpcResponse.SUCCESS == response.getCode()) {
                        return response.getResult();
                    }
                    throw response.getThrowable();

                // 异步模式下，直接强转返回值为 TripleFuture 即可
                case ASYNC:
                    return tripleFuture;
            }
            throw new TripleRpcException("invokeType can't be null");
        }));
    }
}
