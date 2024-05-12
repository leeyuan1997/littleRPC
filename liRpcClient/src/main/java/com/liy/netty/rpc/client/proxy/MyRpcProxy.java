package com.liy.netty.rpc.client.proxy;

import com.liy.netty.rpc.client.RpcClient;
import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;
import com.liy.netty.rpc.utils.RpcUtils;

import java.lang.reflect.Method;

public class MyRpcProxy {
    RpcClient client;
    public MyRpcProxy(RpcClient client) {
        this.client = client;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> serviceInterface) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class[]{serviceInterface},
                (proxy, method, args) -> invoke(serviceInterface, method, args)
        );
    }
    private Object invoke(Class<?> serviceInterface, Method method, Object[] args) throws Throwable {
        // 创建 RpcRequest 对象
        RpcRequest request = new RpcRequest();
        request.setRequestId(RpcUtils.generateRequestId());
        request.setInterfaceName(serviceInterface.getName());
        request.setMehodName(method.getName());
        request.setParametersType(method.getParameterTypes());
        request.setArgs(args);
        request.setMessageTypeId(1);
        request.setSerializerTypeId(1);

        // 发送请求并获取响应
        RpcResponse response = client.sendRequest(request);

        System.out.println(response.getResult());
        return response.getResult();
        // 处理响应结果
//        if (response.getCode() == 0) {
//            return response.getData();
//        } else {
//            throw new RuntimeException("Error: " + response.getMessage());
//        }
    }
}
