package com.liy.netty.test;

import com.liy.netty.rpc.client.RpcClient;
import com.liy.netty.rpc.client.proxy.MyRpcProxy;
import com.liy.netty.rpc.server.LiRpcServer;
import com.liy.netty.rpc.service.HelloService;
import org.junit.Test;

public class TestConnect {
    @Test
    public void startServer(){
        LiRpcServer server = new LiRpcServer(9090);
        server.run();
    }
    @Test
    public void startClient(){
        RpcClient client = new RpcClient();
        MyRpcProxy proxy = new MyRpcProxy(client);
        HelloService proxy1 = proxy.createProxy(HelloService.class);
        System.out.println(proxy1.add(1, 2, 3));
    }
}
