package com.liy.netty.test;

import com.liy.netty.rpc.client.RpcClient;
import com.liy.netty.rpc.client.proxy.MyRpcProxy;
import com.liy.netty.rpc.server.LiRpcServer;
import com.liy.netty.rpc.service.HelloService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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

    @Test
    public void startMoreClients(){
        ExecutorService service =Executors.newFixedThreadPool(10);
        List<Future<?>> futureList = new ArrayList<>();
        RpcClient client = new RpcClient();
        MyRpcProxy proxy = new MyRpcProxy(client);
        for(int i = 0; i < 10;i++) {

            int finalI = i;
            Future<?> submit = service.submit(() -> {
                HelloService proxy1 = proxy.createProxy(HelloService.class);
                System.out.println(proxy1.add(finalI,finalI+1,finalI+2));
                try {
                    Thread.sleep(1000);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            futureList.add(submit);
        }
        for(Future<?> future: futureList) {
            try {
                future.get();
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        service.shutdown();

    }
}
