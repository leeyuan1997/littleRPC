package com.liy.netty.rpc.client;

import com.liy.netty.rpc.client.connection.ConnectionPool;
import com.liy.netty.rpc.client.handler.MyLengthFieldBasedFrameDecoder;
import com.liy.netty.rpc.client.handler.RequestEncoder;
import com.liy.netty.rpc.client.handler.ResponseDecoder;
import com.liy.netty.rpc.client.handler.RpcClientHandler;
import com.liy.netty.rpc.protcol.impl.RpcRequest;
import com.liy.netty.rpc.protcol.impl.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RpcClient {
    List<Channel> channelList;
    ConnectionPool connectionPool;
    public RpcClient() {
        channelList = new ArrayList<>();
        String zkaddress="127.0.0.1:2181";
        connectionPool = new ConnectionPool(zkaddress);
    }

    public RpcResponse sendRequest(RpcRequest request) throws ExecutionException, InterruptedException {
        String serviceName = request.getInterfaceName();
        Channel channel = getChannel(serviceName);
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        //获取对应的handler
        RpcClientHandler rpcClientHandler = channel.pipeline().get(RpcClientHandler.class);
        rpcClientHandler.setFuture(request.getRequestId(),future);
        channel.writeAndFlush(request);
        RpcResponse rpcResponse = future.get();
        return  rpcResponse;
    }

    Channel getChannel(String serviceName){
        return connectionPool.getConnection(serviceName);
    }
}

