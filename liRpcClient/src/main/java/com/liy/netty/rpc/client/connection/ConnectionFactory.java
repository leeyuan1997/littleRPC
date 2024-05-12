package com.liy.netty.rpc.client.connection;

import com.liy.netty.rpc.client.handler.MyLengthFieldBasedFrameDecoder;
import com.liy.netty.rpc.client.handler.RequestEncoder;
import com.liy.netty.rpc.client.handler.ResponseDecoder;
import com.liy.netty.rpc.client.handler.RpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.List;

public class ConnectionFactory {
    private final Bootstrap bootstrap;
    EventLoopGroup workerGroup = new NioEventLoopGroup();

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
            return bootstrap.connect(host,port).sync().channel();
        }catch (Exception e) {
            throw  new RuntimeException("连接失败");
        }
    }
}
