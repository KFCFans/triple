package com.tjq.triple.transport.netty4;

import com.tjq.triple.serialize.Serializer;
import com.tjq.triple.transport.netty4.codec.NettyDecoder;
import com.tjq.triple.transport.netty4.codec.NettyEncoder;
import com.tjq.triple.transport.netty4.handler.NettyRpcRequestHandler;
import com.tjq.triple.transport.netty4.handler.NettyRpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;

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
    private final Serializer serializer;
    private final Executor localInvokerPool;

    public NettyClientBootstrap(String serverIp, int serverPort, Serializer serializer, Executor localInvokerPool) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.serializer = serializer;
        this.localInvokerPool = localInvokerPool;
    }

    private EventLoopGroup workerGroup;
    private Bootstrap client;
    private volatile boolean started = false;

    public boolean start() {
        if (started) {
            return true;
        }
        // 默认线程数，CPU核心数 * 2
        workerGroup = new NioEventLoopGroup();
        client = new Bootstrap();
        client.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new NettyDecoder(serializer))
                                .addLast(new NettyEncoder(serializer))
                                .addLast(new NettyRpcRequestHandler(localInvokerPool))
                                .addLast(new NettyRpcResponseHandler());
                    }
                });
        started = true;
        // 连接 server
        try {
            // 阻塞在这里，直到通道关闭
            ChannelFuture channel = client.connect(serverIp, serverPort).sync();
            channel.channel().closeFuture().sync();
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
