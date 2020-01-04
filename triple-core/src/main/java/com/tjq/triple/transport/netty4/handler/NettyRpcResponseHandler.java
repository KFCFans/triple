package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.consumer.response.FuturePool;
import com.tjq.triple.consumer.response.TripleFuture;
import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC 响应处理
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public class NettyRpcResponseHandler extends SimpleChannelInboundHandler<TripleRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TripleRpcResponse response) throws Exception {
        TripleFuture tripleFuture = FuturePool.get(response.getRequestId());
        if (tripleFuture != null) {
            tripleFuture.finishedInvoke(response);
        }else {
            log.error("[Triple] received unknown rpc response({})", response);
        }
    }
}
