package com.liy.netty.rpc.server.registry;

public interface ServiceSaver {
    <T> void register(Class<T> serviceInterface, T serviceInstance);
    <T> T getService(Class<T> serviceInterface);
}