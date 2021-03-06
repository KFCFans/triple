package com.tjq.triple.transport.netty4;


import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.common.utils.AddressUtils;
import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.transport.Transporter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * 数据发送器
 *
 * @author tjq
 * @since 2020/1/3
 */
@AllArgsConstructor
public class NettyTransporter implements Transporter {

    // Netty 数据通道
    private final Channel channel;

    @Override
    public String remoteAddress() {
        return AddressUtils.getAddress(channel);
    }

    @Override
    public boolean available() {
        return channel.isActive();
    }

    @Override
    public void sendSync(TripleProtocol protocol, long timeoutMS) throws TripleRpcException {
        ChannelFuture future = channel.writeAndFlush(protocol);
        try {
            future.get(timeoutMS, TimeUnit.MILLISECONDS);
        }catch (Exception e) {
            processFailed(protocol);
            throw new TripleRpcException("time out", e);
        }
    }

    @Override
    public void sendAsync(TripleProtocol protocol) {
        ChannelFuture channelFuture = channel.writeAndFlush(protocol);
        channelFuture.addListener(f -> {
            if (!f.isSuccess()) {
                processFailed(protocol);
            }
        });
    }
}
