package com.liy.netty.rpc.server;

import com.liy.netty.rpc.server.handler.RequestDecoder;
import com.liy.netty.rpc.server.handler.RequestHandler;
import com.liy.netty.rpc.server.handler.ResponseEncoder;
import com.liy.netty.rpc.server.registry.DefaultServiceRegistry;
import com.liy.netty.rpc.server.registry.ServiceRegistry;
import com.liy.netty.rpc.server.serviceimpl.ServerHelloServiceImpl;
import com.liy.netty.rpc.service.HelloService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LiRpcServer implements Runnable{

    private int port;
    ServiceRegistry registry = new DefaultServiceRegistry();
    public LiRpcServer(int port) {
        this.port = port;
    }

    public void run() {
        registry.register(HelloService.class,new ServerHelloServiceImpl());
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
                                    .addLast(new RequestDecoder())

                                    .addLast(new ResponseEncoder())
                                    .addLast(new RequestHandler(registry));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


}
