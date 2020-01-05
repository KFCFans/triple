package com.tjq.triple.transport.netty4;

import com.tjq.triple.transport.Transporter;
import com.tjq.triple.transport.TransporterPool;
import com.tjq.triple.transport.netty4.handler.NettyChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;


/**
 * Netty Client 连接器
 *
 * @author tjq
 * @since 2020/1/4
 */
@Slf4j
public class NettyClientBootstrap {

    private final String serverIp;
    private final int serverPort;

    public NettyClientBootstrap(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    private EventLoopGroup workerGroup;
    private volatile boolean started = false;

    public boolean start() {
        if (started) {
            return true;
        }
        // 默认线程数，CPU核心数 * 2
        workerGroup = new NioEventLoopGroup();
        Bootstrap client = new Bootstrap();
        client.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyChannelInitializer());
        started = true;
        // 连接 server
        try {
            ChannelFuture future = client.connect(serverIp, serverPort).sync();
            log.info("[TripleClient] netty client connect to {}:{} success", serverIp, serverPort);
            // 阻塞在这里，直到通道关闭
            Transporter transporter = new NettyTransporter(future.channel());
            TransporterPool.addTransporter(transporter);
//            channel.sync();
//            channel.channel().closeFuture().sync();
        }catch (Exception e) {
            started = false;
            log.error("[TripleClient] netty client startup failed.", e);
            stop();
        }
        return started;
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
