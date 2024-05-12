package com.liy.netty.rpc.serialize;

import com.liy.netty.rpc.serialize.impl.JSONSerializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerFactory {
    private static final Map<Integer,Serializer> map = new ConcurrentHashMap<>();

    public static Serializer getSerializer(int serializerId) {
        SerializerType type = createSerializer(serializerId);
        if(type==SerializerType.JSON) {
            return JSONSerializerHolder.INSTANCE;
        }else{
            throw new IllegalArgumentException(type+":该序列化方式没有实现");
        }

    }
    private static SerializerType createSerializer(int serializerId) {
        SerializerType type = SerializerType.fromId(serializerId);
        if(type==null) {

            throw new IllegalArgumentException("没有这种序列化方式哥们");
        }
        return type;
    }

    private static class JSONSerializerHolder{
        private static final Serializer INSTANCE = new JSONSerializer();
    }
}


