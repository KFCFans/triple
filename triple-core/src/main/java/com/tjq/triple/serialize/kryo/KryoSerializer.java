package com.tjq.triple.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;
import com.tjq.triple.common.exception.TripleRpcException;
import com.tjq.triple.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * kryo 序列化（triple 默认序列化方式）
 *
 * @author tjq
 * @since 2020/1/4
 */
@Slf4j
public class KryoSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        Kryo kryo = kryoLocal.get();
        try(
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Output output = new Output(byteArrayOutputStream)
        ) {
            kryo.writeClassAndObject(output, obj);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("[Triple] serialize by kryo failed.", e);
            throw new TripleRpcException("serialize by kryo failed", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = kryoLocal.get();
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                Input input = new Input(byteArrayInputStream)
        ) {
            return (T) kryo.readClassAndObject(input);
        }catch (Exception e) {
            log.error("[Triple] deserialize by kryo failed.", e);
            throw new TripleRpcException("deserialize by kryo failed", e);
        }
    }

    // Kryo 实例线程不安全
    private static final ThreadLocal<Kryo> kryoLocal = ThreadLocal.withInitial(() -> {

        Kryo kryo = new Kryo();
        // 关闭序列化注册，会导致性能些许下降，但在分布式环境中，注册类生成ID不一致会导致错误
        kryo.setRegistrationRequired(false);
        // 支持循环引用，也会导致性能些许下降 T_T
        kryo.setReferences(true);
        // 增加异常序列化器
        kryo.addDefaultSerializer(Throwable.class, new JavaSerializer());
        return kryo;
    });
}
