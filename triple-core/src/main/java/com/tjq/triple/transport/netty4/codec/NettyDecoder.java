package com.tjq.triple.transport.netty4.codec;

import com.tjq.triple.protocol.TripleProtocol;
import com.tjq.triple.protocol.TripleRpcCMD;
import com.tjq.triple.protocol.TripleTransportObject;
import com.tjq.triple.serialize.SerializerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * decoder
 * 循环读高效还是这样返回 return 高效？小型数据差距应该不大，大对象传输需要做实验验证，理论上应该是循环效率高
 *
 * @author tjq
 * @since 2020/1/3
 */
public class NettyDecoder extends ByteToMessageDecoder {

    // 协议首部定长 8字节
    private static final int PROTOCOL_HEAD_LENGTH = 8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < PROTOCOL_HEAD_LENGTH) {
            return;
        }
        in.markReaderIndex();

        short magic = in.readShort();
        byte ext = in.readByte();
        byte cmd = in.readByte();
        int length = in.readInt();

        // 如果此时发生粘包，后面的 RPC 请求可能也会被丢弃，不过最终可恢复
        if (magic != TripleProtocol.TRIPLE_PROTOCOL_MAGIC) {
            in.skipBytes(in.readableBytes());
            return;
        }

        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);

        // 序列化
        Class<? extends TripleTransportObject> clazz = TripleRpcCMD.of(cmd).getClazz();
        out.add(SerializerFactory.getSerializer().deserialize(data, clazz));
    }

}
