package com.tjq.triple.consumer;

import com.google.common.collect.Lists;
import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.bootstrap.config.TripleRegistryConfig;
import com.tjq.triple.common.enums.TripleRegisterType;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.consumer.response.TripleFuture;
import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import com.tjq.triple.transport.Transporter;
import com.tjq.triple.transport.TransporterPool;
import com.tjq.triple.transport.netty4.client.NettyClient;
import com.tjq.triple.transport.netty4.client.NettyConnectionFactory;
import lombok.Setter;
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

    // 注册中心配置
    @Setter
    private TripleRegistryConfig registryConfig;
    @Setter
    private String groupName;
    @Setter
    private String version;
    @Setter
    private Class<?> interfaceClass;
    @Setter
    private String interfaceName;
    // 同步/异步
    @Setter
    private boolean async;


    public TripleReferenceBean() {

        groupName = "Triple";
        version = "1.0.0";
        async = false;
    }

    @SuppressWarnings("unchecked")
    public T get() throws Exception{

        if (ref == null) {
            synchronized (this) {
                if (ref == null) {
                    // 参数校验
                    check();
                    // 初始化连接
                    initConnection();
                    // 生成代理对象
                    ref = (T) genProxyObject();
                }
            }
        }
        return ref;
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
                // TODO：连接 ZK & 读取 provider 信息 & 读取配置信息（序列化方式等）
                break;
            case DIRECT:
                registryConfig.getAddressList().forEach(ads -> {
                    Transporter transporter = TransporterPool.getTransporter(ads);
                    if (transporter == null || !transporter.available()) {
                        needConnectionAddress.add(ads);
                    }
                });

        }

        // 2. 建立连接
        needConnectionAddress.forEach(targetAddress -> {
            String[] split = targetAddress.split(":");
            String ip = split[0];
            int port = Integer.parseInt(split[1]);
            switch (TripleGlobalConfig.getProtocol()) {
                case TRIPLE_NETTY:
                    NettyConnectionFactory.rebuild();
                    break;
                case TRIPLE_HTTP:
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
            Transporter transporter = null;
            if (registryConfig.getRegisterType() == TripleRegisterType.DIRECT) {
                for (String ads : registryConfig.getAddressList()) {
                    transporter = TransporterPool.getTransporter(ads);
                    if (transporter != null) {
                        break;
                    }
                }
            }else {
                transporter = TransporterPool.getTransporter();
            }
            if (transporter == null) {
                // TODO：重连（直接调用 initConnection ？）
            }
            transporter.sendAsync(TripleProtocol.buildRpcRequest(rpcRequest));

            // 4. 处理返回结果
            if (async) {
                return null;
            }
            // 超时会抛出异常，继续向外抛
            TripleRpcResponse response = tripleFuture.get(TripleGlobalConfig.getTimeoutMS(), TimeUnit.MILLISECONDS);
            switch (response.getCode()) {
                case TripleRpcResponse.SUCCESS : return response.getResult();
                case TripleRpcResponse.INVOKE_SUCCESS_EXECUTE_FAILED: throw response.getThrowable();
                default:
                    // 启动集群容错模式
                    return null;
            }

        }));
    }

    private void check() {
        if (registryConfig == null) {
            throw new TripleRpcException("please set register info");
        }
    }
}
