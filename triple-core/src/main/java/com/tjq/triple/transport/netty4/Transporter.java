package com.tjq.triple.transport.netty4;

import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.protocol.TripleProtocol;
import io.netty.channel.ChannelFuture;

/**
 * 连接器
 *
 * @author tjq
 * @since 2020/1/3
 */
public interface Transporter {

    Object sendSync(TripleProtocol protocol, long timeout) throws TripleRpcException;

    ChannelFuture sendAsync(TripleProtocol protocol);

}
