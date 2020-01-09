package com.tjq.triple.transport.netty4.handler;

import com.tjq.triple.common.utils.AddressUtils;
import com.tjq.triple.transport.Transporter;
import com.tjq.triple.transport.TransporterPool;
import com.tjq.triple.transport.netty4.NettyTransporter;
import com.tjq.triple.transport.netty4.client.NettyConnectionFactory;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 完成活跃 channel 的注册
 *
 * @author tjq
 * @since 2020/1/5
 */
@Slf4j
@AllArgsConstructor
public class NettyChannelDuplexHandler extends ChannelDuplexHandler {

    private final boolean isClient;

    /**
     * channel 活跃的回调
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Transporter transporter = new NettyTransporter(ctx.channel());
        TransporterPool.addTransporter(transporter);
        super.channelActive(ctx);
    }

    /**
     * channel 不活跃的回调
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        String address = AddressUtils.getAddress(ctx.channel());

        // 删除传输器
        TransporterPool.removeTransporter(address);
        // 重新建立 provider 连接
        if (isClient) {
            log.info("[Triple] channel of remote({}) is inactive, try to reconnect to available provider", address);
            NettyConnectionFactory.loseProviders(address);
        }

        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("[Triple] netty channel exceptionCaught", cause);
    }
}
