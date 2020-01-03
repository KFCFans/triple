package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.rpc.TripleRpcRequest;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import com.tjq.triple.provider.ProviderFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * RPC 请求处理
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<TripleRpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TripleRpcRequest msg) throws Exception {
        // TODO：线程池执行
        ctx.writeAndFlush(TripleProtocol.buildRpcResponse(invoke(msg)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        log.error("[Triple] netty channel caught exception", cause);
        ctx.close();
    }

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
        }catch (Exception e) {
            response.setCode(TripleRpcResponse.INVOKE_SUCCESS_EXECUTE_FAILED);
            // 暂时先将堆栈 String 化进行传递，后期尝试直接序列化 exception
            response.setMessage(ExceptionUtils.getStackTrace(e));
        }
        return response;
    }
}
