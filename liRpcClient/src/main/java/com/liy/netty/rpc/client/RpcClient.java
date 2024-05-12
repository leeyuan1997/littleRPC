package com.liy.netty.rpc.client;

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
    public RpcClient() {
        channelList = new ArrayList<>();
        String host = "localhost";
        int port = 9090;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new MyLengthFieldBasedFrameDecoder())
                            .addLast(new RequestEncoder())
                            .addLast(new ResponseDecoder())
                            .addLast(new RpcClientHandler());
                }
            });

            // Start the client.
            Channel channel = b.connect(host, port).sync().channel();// (5)
            channelList.add(channel);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
//            workerGroup.shutdownGracefully();
        }
    }

    public RpcResponse sendRequest(RpcRequest request) throws ExecutionException, InterruptedException {
        String serviceName = request.getInterfaceName();
        Channel channel = getChannel(serviceName);
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        RpcClientHandler rpcClientHandler = channel.pipeline().get(RpcClientHandler.class);
        rpcClientHandler.setFuture(future);
        channel.writeAndFlush(request);
        return future.get();
    }

    Channel getChannel(String serviceName){
        return channelList.get(0);
    }
}
