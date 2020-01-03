package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.protocol.rpc.TripleRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * RPC 响应处理
 *
 * @author tjq
 * @since 2020/1/3
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<TripleRpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TripleRpcResponse msg) throws Exception {

    }
}
