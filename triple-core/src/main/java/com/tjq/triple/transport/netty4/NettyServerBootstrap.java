package com.tjq.triple.transport.netty4;

import com.tjq.triple.transport.netty4.handler.NettyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty Server 服务器
 *
 * @author tjq
 * @since 2020/1/3
 */
@Slf4j
public class NettyServerBootstrap {

    /**
     * config info
     */
    private final String ip;
    private final int port;

    public NettyServerBootstrap(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private volatile boolean started = false;

    public boolean start() {
        if (started) {
            return true;
        }
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new NettyChannelInitializer());
        started = true;
        try {
            ChannelFuture channelFuture = server.bind(ip, port).sync();
            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
        }catch (Exception e) {
            started = false;
            stop();
            log.error("[TripleServer] netty server startup failed.", e);
        }
        return started;
    }

    /**
     * 关闭 Netty 服务器
     */
    public void stop() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
