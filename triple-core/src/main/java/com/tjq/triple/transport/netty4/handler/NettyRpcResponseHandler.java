package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.common.utils.AddressUtils;
import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.consumer.response.TripleFuture;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import com.tjq.triple.transport.netty4.client.NettyConnectionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.tjq.triple.protocol.rpc.TripleRpcResponse.*;

/**
 * RPC 响应处理
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
@AllArgsConstructor
public class NettyRpcResponseHandler extends SimpleChannelInboundHandler<TripleRpcResponse> {

    private final boolean isClient;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TripleRpcResponse response) throws Exception {
        short code = response.getCode();
        switch (code) {
            case SUCCESS:
            case INVOKE_SUCCESS_EXECUTE_FAILED:
                TripleFuture tripleFuture = FuturePool.get(response.getRequestId());
                if (tripleFuture != null) {
                    tripleFuture.finishedInvoke(response);
                }else {
                    log.warn("[Triple] received unknown rpc response({})", response);
                }
                break;
            case PROVIDER_OVERLOAD:
                // 超出单台 provider 的连接能力，需要构建多个连接
                if (isClient) {
                    // TODO：构建多个连接（扩容）
                }
                break;
            default:
                // 启动集群容错机制，连接其他的 provider
                log.warn("[Triple] rpc invoke failed, code = " + code);
                if (isClient) {
                    String address = AddressUtils.getAddress(ctx.channel());
                    NettyConnectionFactory.loseProviders(address);
                }
        }
    }
}
