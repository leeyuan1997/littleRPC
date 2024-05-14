package com.liy.netty.rpc.server.handler;

import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;
import com.liy.netty.rpc.server.registry.ServiceSaver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {
    ServiceSaver registry;
    public RequestHandler(ServiceSaver registry) {
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

