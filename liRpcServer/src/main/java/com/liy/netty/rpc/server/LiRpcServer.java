package com.liy.netty.rpc.server;

import com.liy.netty.rpc.server.handler.RequestDecoder;
import com.liy.netty.rpc.server.handler.RequestHandler;
import com.liy.netty.rpc.server.handler.ResponseEncoder;
import com.liy.netty.rpc.server.registry.DefaultServiceSaver;
import com.liy.netty.rpc.server.registry.ServiceSaver;
import com.liy.netty.rpc.server.serviceimpl.ServerHelloServiceImpl;
import com.liy.netty.rpc.service.HelloService;
import com.liy.netty.rpc.serviceZooKeeper.ServiceRegistry;
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
    ServiceSaver saver = new DefaultServiceSaver();
    private  String ip;
    ServiceRegistry zkRegistry;
    public LiRpcServer(String ip,int port) {
        this.ip = ip;
        this.port = port;
        String zkaddress="127.0.0.1:2181";
        zkRegistry = new ServiceRegistry(zkaddress);
    }

    public void run() {
        //注册服务
        RegisterService(HelloService.class,new ServerHelloServiceImpl());
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
                                    .addLast(new RequestHandler(saver));
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

    public void RegisterService(Class<?>clazz,Object instance){
        saver.register(HelloService.class,new ServerHelloServiceImpl());
        zkRegistry.registerService(clazz.getName(),String.join(":",new String[]{ip,String.valueOf(port)}));
    }

}
