package com.tjq.triple.transport.netty4;

import com.tjq.triple.serialize.Serializer;
import com.tjq.triple.transport.netty4.codec.NettyDecoder;
import com.tjq.triple.transport.netty4.codec.NettyEncoder;
import com.tjq.triple.transport.netty4.handler.NettyRpcRequestHandler;
import com.tjq.triple.transport.netty4.handler.NettyRpcResponseHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

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
    private final Serializer serializer;
    private final Executor serverThreadPool;

    public NettyServerBootstrap(String ip, int port, Serializer serializer, Executor serverThreadPool) {
        this.ip = ip;
        this.port = port;
        this.serializer = serializer;
        this.serverThreadPool = serverThreadPool;
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
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new NettyDecoder(serializer))
                                .addLast(new NettyEncoder(serializer))
                                .addLast(new NettyRpcRequestHandler(serverThreadPool))
                                .addLast(new NettyRpcResponseHandler());
                    }
                });
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
