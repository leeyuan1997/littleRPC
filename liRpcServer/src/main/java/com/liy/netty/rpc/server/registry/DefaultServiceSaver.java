package com.liy.netty.rpc.server.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceSaver implements ServiceSaver {
    private final Map<Class<?>, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public <T> void register(Class<T> serviceInterface, T serviceInstance) {
        serviceMap.put(serviceInterface, serviceInstance);
    }

    @Override
    public <T> T getService(Class<T> serviceInterface) {
        return (T) serviceMap.get(serviceInterface);
    }
}