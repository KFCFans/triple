package com.tjq.triple.transport.netty4;

import com.tjq.triple.common.TripleStore;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.protocol.TripleProtocol;
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
public class DefaultTransporter implements Transporter {

    private final Channel channel;

    @Override
    public Object sendSync(TripleProtocol protocol, long timeout) throws TripleRpcException {
        ChannelFuture future = channel.writeAndFlush(protocol);
        TripleStore<Object> store = new TripleStore<>();
        future.addListener(finishedFuture -> store.setData(finishedFuture.getNow()));
        try {
            future.get(timeout, TimeUnit.MILLISECONDS);
            return store.getData();
        }catch (Exception e) {
            return new TripleRpcException(String.format("time out in %dms", timeout), e);
        }
    }

    @Override
    public ChannelFuture sendAsync(TripleProtocol protocol) {
        return channel.writeAndFlush(protocol);
    }
}
