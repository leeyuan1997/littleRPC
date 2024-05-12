package com.liy.netty.rpc.serialize;

public interface Serializer {

    public byte[] encode(Object in);

    public <T> T parseObject(byte[] in,Class<T>outInstancetype);
}
