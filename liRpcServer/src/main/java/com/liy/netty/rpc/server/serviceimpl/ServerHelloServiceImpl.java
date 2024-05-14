package com.liy.netty.rpc.server.serviceimpl;

import com.liy.netty.rpc.service.HelloService;
@RpcService(HelloService.class)
public class ServerHelloServiceImpl implements HelloService {
    @Override
    public int add(int a, int b) {
        return a+b;
    }

    @Override
    public int add(int a, int b, int c) {
        return a+b+c;
    }

    @Override
    public int add(int... a) {
        int sum=0;
        for(var i:a) {
            sum+=i;
        }
        return sum;
    }

    @Override
    public int sub(int a, int b) {
        return a-b;
    }
}
