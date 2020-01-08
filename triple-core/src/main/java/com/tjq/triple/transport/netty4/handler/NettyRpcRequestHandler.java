package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import com.tjq.triple.provider.ProviderFactory;
import com.tjq.triple.provider.ProviderThreadPool;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

/**
 * RPC 请求处理
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<TripleRpcRequest> {

    private final Executor pool;

    public NettyRpcRequestHandler() {
        pool = ProviderThreadPool.getPool();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TripleRpcRequest msg) throws Exception {
        // writeAndFlush 为异步操作，异常处理放在 Transport 层实现
        try {
            pool.execute(() -> ctx.writeAndFlush(TripleProtocol.buildRpcResponse(invoke(msg))));
        } catch (RejectedExecutionException re) {
            log.warn("[Triple] thread pool is full load, request will be drop");

            // 线程池满，启动负载均衡策略
            TripleRpcResponse response = TripleRpcResponse.failed(msg.getContext().getRequestId(), TripleRpcResponse.PROVIDER_OVERLOAD, re);
            ctx.writeAndFlush(TripleProtocol.buildRpcResponse(response));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("[Triple] netty channel caught exception", cause);
        ctx.close();
    }

    /**
     * 本地调用，包装结果
     */
    private TripleRpcResponse invoke(TripleRpcRequest rpcRequest) {

        String group = rpcRequest.getGroup();
        String version = rpcRequest.getVersion();

        String className = rpcRequest.getClassName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();

        TripleRpcResponse response = new TripleRpcResponse();
        response.setRequestId(rpcRequest.getContext().getRequestId());
        try {
            Object res = ProviderFactory.invoke(group, version, className, methodName, parameterTypes, parameters);
            response.setCode(TripleRpcResponse.SUCCESS);
            response.setResult(res);
        }catch (Throwable t) {
            response.setCode(TripleRpcResponse.INVOKE_SUCCESS_EXECUTE_FAILED);
            response.setThrowable(t);
        }
        return response;
    }
}
