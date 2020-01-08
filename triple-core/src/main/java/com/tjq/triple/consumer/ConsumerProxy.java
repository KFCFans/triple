package com.tjq.triple.consumer;

import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.consumer.response.TripleFuture;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import com.tjq.triple.registry.ProviderAddressPool;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 生成 consumer 的代理对象
 *
 * @author tjq
 * @since 2020/1/8
 */
@AllArgsConstructor
public class ConsumerProxy implements InvocationHandler {

    private final ConsumerBeanInfo consumerBean;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String group = consumerBean.getGroup();
        String serviceName = consumerBean.getServiceName();
        String version = consumerBean.getVersion();

        // 1. 构造 RPC 请求
        TripleRpcRequest rpcRequest = new TripleRpcRequest();
        rpcRequest.setGroup(group);
        rpcRequest.setVersion(version);
        rpcRequest.setClassName(serviceName);
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);

        // 2. 构造异步响应结果（双写，pool + threadLocal）
        TripleFuture tripleFuture = FuturePool.push(rpcRequest);

        // 3. 发送 RPC 网络请求


        // 4. 处理返回结果
        if (consumerBean.isAsync()) {
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
    }
}
