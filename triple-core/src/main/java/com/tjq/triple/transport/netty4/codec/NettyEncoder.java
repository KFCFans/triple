package com.tjq.triple.transport.netty4.codec;

import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * encoder
 *
 * @author tjq
 * @since 2020/1/3
 */
@AllArgsConstructor
public class NettyEncoder extends MessageToByteEncoder<TripleProtocol> {

    private final Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext ctx, TripleProtocol msg, ByteBuf out) throws Exception {

        // 序列化
        byte[] data = serializer.serialize(msg.getData());

        // 协议头
        out.writeShort(msg.getMagic());
        out.writeByte(msg.getExtension());
        out.writeByte(msg.getCmd());
        out.writeInt(data.length);

        // 数据
        out.writeBytes(data);
    }
}
