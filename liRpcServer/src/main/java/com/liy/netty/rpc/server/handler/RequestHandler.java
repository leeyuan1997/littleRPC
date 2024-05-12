package com.liy.netty.rpc.server.handler;

import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;
import com.liy.netty.rpc.server.registry.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    ServiceRegistry registry;
    public RequestHandler(ServiceRegistry registry) {
        this.registry = registry;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        //获取请求并处理
        String interfaceName = rpcRequest.getInterfaceName();
        String mehodName = rpcRequest.getMehodName();
        Object o = registry.getService(Class.forName(interfaceName));
        Method method = o.getClass().getMethod(mehodName, rpcRequest.getParametersType());
        Object result = method.invoke(o, rpcRequest.getArgs());

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult(result);
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        rpcResponse.setMessageTypeId(2);
        rpcResponse.setSerializerTypeId(rpcRequest.getSerializerTypeId());
        ctx.channel().writeAndFlush(rpcResponse);
    }
}

