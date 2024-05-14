package com.liy.netty.rpc.client.connection;

import com.liy.netty.rpc.client.handler.MyLengthFieldBasedFrameDecoder;
import com.liy.netty.rpc.client.handler.RequestEncoder;
import com.liy.netty.rpc.client.handler.ResponseDecoder;
import com.liy.netty.rpc.client.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ConnectionFactory {
    private final Bootstrap bootstrap;
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    final int MAXRETRY = 2;
    public ConnectionFactory() {

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
        bootstrap=b;
    }
    public Channel createChannel(String serviceAddress) {
        String[] addressParts = serviceAddress.split(":");
        String host = addressParts[0];
        int port = Integer.parseInt(addressParts[1]);
        try {
            CompletableFuture<Channel> connectFuture = connect(host, 4545,0).exceptionally(ex -> {
                System.out.println("ds");
                // 异常处理，抛出更具体的异常或返回null
                throw new RuntimeException("连接失败: " + ex.getMessage(), ex);
            });
                return connectFuture.join();
        }catch (Exception e) {
            throw  new RuntimeException("连接失败");
        }
    }

    public CompletableFuture<Channel> connect(String host,int port,int retryTime) {

        if(retryTime>MAXRETRY){
                CompletableFuture<Channel> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(new Exception("Reached maximum number of retries"));
            return failedFuture;
        }
        CompletableFuture<Channel> connectFuture = new CompletableFuture<>();
        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Connected to server");
                    connectFuture.complete(future.channel());
                } else {
                    System.out.println("Failed to connect to server, trying to reconnect...");
                    future.channel().eventLoop().schedule(() -> {
                        connect(host,port,retryTime+1).whenComplete(((channel, throwable) -> {
                                if(throwable != null) {
                                    connectFuture.completeExceptionally(throwable);
                                }else {
                                    connectFuture.complete(channel);
                                }
                        }));
                    }, 2, TimeUnit.SECONDS);
                }
            }
        });
        return connectFuture;
    }
}
