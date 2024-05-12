package com.liy.netty.rpc.client.handler;

import com.liy.netty.rpc.client.RpcClient;
import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {
//    private CompletableFuture<RpcResponse> future;
    Map<Integer,CompletableFuture<RpcResponse>>requestFutureMap;
    public RpcClientHandler(){
        requestFutureMap = new ConcurrentHashMap<>();
    }
    public void setFuture(int requestId,CompletableFuture<RpcResponse> future) {

        requestFutureMap.put(requestId,future);
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            RpcResponse response = (RpcResponse) msg;
            CompletableFuture<RpcResponse> future = requestFutureMap.get(response.getRequestId());
            future.complete(response);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}