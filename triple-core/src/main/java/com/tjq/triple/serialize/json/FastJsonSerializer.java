package com.tjq.triple.serialize.json;

import com.alibaba.fastjson.JSON;
import com.tjq.triple.serialize.Serializer;

/**
 * FastJSON 序列化
 *
 * @author tjq
 * @since 2020/1/5
 */
public class FastJsonSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(bytes, clazz);
    }
}
