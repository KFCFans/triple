package com.tjq.triple.transport.netty4.client;

import com.tjq.triple.transport.netty4.handler.NettyChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * Netty Client 连接器
 * 注：每一个NettyClient实例都会起一组 EventGroup 线程进行轮询，建议复用，只存在一个 NettyClient 实例
 *
 * @author tjq
 * @since 2020/1/4
 */
@Slf4j
public class NettyClient {

    private Bootstrap client;
    private EventLoopGroup workerGroup;

    public NettyClient() {
        // 默认线程数，CPU核心数 * 2
        workerGroup = new NioEventLoopGroup();
        client = new Bootstrap();
        client.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyChannelInitializer(true));
    }

    /**
     * 连接远程服务器
     */
    public Channel connect(String ip, int port) throws Exception {
        ChannelFuture channelFuture = client.connect(ip, port).sync();
        log.info("[Triple] netty client connect to {}:{} success", ip, port);
        return channelFuture.channel();
    }

    /**
     * 关闭 Netty
     */
    public void stop() {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

}
