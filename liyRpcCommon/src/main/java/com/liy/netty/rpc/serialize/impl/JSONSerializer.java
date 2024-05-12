package com.liy.netty.rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.liy.netty.rpc.serialize.Serializer;

public class JSONSerializer implements Serializer {

    @Override
    public byte[] encode(Object in) {
        return JSON.toJSONBytes(in);
    }

    @Override
    public <T> T parseObject(byte[] in, Class<T> outInstancetype) {
       return JSON.parseObject(in,outInstancetype);
    }
}

