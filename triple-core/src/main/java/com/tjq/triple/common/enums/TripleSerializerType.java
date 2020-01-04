package com.tjq.triple.common.enums;

import com.tjq.triple.serialize.Serializer;
import com.tjq.triple.serialize.java.JavaSerializer;
import com.tjq.triple.serialize.kryo.KryoSerializer;

/**
 * 序列化方式
 *
 * @author tjq
 * @since 2020/1/4
 */
public enum TripleSerializerType {

    KRYO,
    JAVA;


    public Serializer getInstance() {
        if (this == KRYO) {
            return new KryoSerializer();
        }

        return new JavaSerializer();
    }

}
