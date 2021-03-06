package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.transport.netty4.codec.NettyDecoder;
import com.tjq.triple.transport.netty4.codec.NettyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Channel 初始化器
 * 由于 triple 是全双工 RPC 框架，因此 server 和 client 的组件完全相同
 *
 * @author tjq
 * @since 2020/1/5
 */
@Slf4j
@AllArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final boolean isClient;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new NettyChannelDuplexHandler(isClient))
                .addLast(new NettyDecoder())
                .addLast(new NettyEncoder())
                .addLast(new NettyRpcRequestHandler())
                .addLast(new NettyRpcResponseHandler(isClient));
    }
}
