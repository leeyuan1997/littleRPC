package com.liy.netty.rpc.server;

import com.liy.netty.rpc.server.handler.RequestDecoder;
import com.liy.netty.rpc.server.handler.RequestHandler;
import com.liy.netty.rpc.server.handler.ResponseEncoder;
import com.liy.netty.rpc.server.registry.DefaultServiceSaver;
import com.liy.netty.rpc.server.registry.ServiceSaver;
import com.liy.netty.rpc.server.serviceimpl.RpcService;
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
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LiRpcServer implements Runnable, BeanPostProcessor {

    private Integer port;

    ServiceSaver saver;
    private  String ip;
    ServiceRegistry zkRegistry;

    private ApplicationContext context;
    @Autowired
    public void setSaver(ServiceSaver saver) {
        this.saver = saver;
    }
    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Map<String, Object> serviceMap = context.getBeansWithAnnotation(RpcService.class);
        for(Object serviceBean: serviceMap.values()){
            Class<?>[] interfaces = serviceBean.getClass().getInterfaces();
            for(Class serviceInterface : interfaces ){
                saver.register((Class<Object>)serviceInterface,serviceBean);
            }
        }
        System.out.println("注册成功");
        return bean;
    }
    @Autowired
    private Environment env;

    public void someMethod() {
        String ip = env.getProperty("server.host");
        Integer port = Integer.parseInt(env.getProperty("server.port"));
        String zkAddress = env.getProperty("zookeeper.host");
        // 使用这些值进行操作
    }

    public LiRpcServer(@Value("${server.host}") String ip,
                       @Value("${server.port}") Integer port,
                       @Value("${zookeeper.host}") String zkAddress) {
        this.ip = ip;
        this.port = port;
        zkRegistry = new ServiceRegistry(zkAddress);
    }

    public void run() {
        //注册服务
        RegisterService(HelloService.class);
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

    public void RegisterService(Class<?>clazz){
//        saver.register(HelloService.class,new ServerHelloServiceImpl());
        zkRegistry.registerService(clazz.getName(),String.join(":",new String[]{ip,String.valueOf(port)}));
    }


}
