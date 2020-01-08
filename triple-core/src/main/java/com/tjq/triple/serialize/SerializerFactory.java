package com.tjq.triple.serialize;

import com.tjq.triple.bootstrap.config.TripleGlobalConfig;
import com.tjq.triple.serialize.java.JavaSerializer;
import com.tjq.triple.serialize.json.FastJsonSerializer;
import com.tjq.triple.serialize.kryo.KryoSerializer;
import lombok.extern.slf4j.Slf4j;

/**
 * 序列化器工厂
 * 单例模式，因此需要 Serializer 保证线程安全
 *
 * @author tjq
 * @since 2020/1/5
 */
@Slf4j
public final class SerializerFactory {

    private static volatile Serializer serializer;

    /**
     * zookeeper 模式下：从ZK读取配置文件一定要先于该方法的第一次调用
     * 直连模式下：一开始就指定了，因此没关系
     */
    public static Serializer getSerializer() {
        if (serializer == null) {
            synchronized (SerializerFactory.class) {
                if (serializer == null) {

                    switch (TripleGlobalConfig.getSerializerType()) {
                        case KRYO: serializer = new KryoSerializer();break;
                        case FAST_JSON: serializer = new FastJsonSerializer();break;
                        default: serializer = new JavaSerializer();break;
                    }
                    log.info("[Triple] initialized the serializer({}) successful", serializer);
                }
            }
        }
        return serializer;
    }
}
