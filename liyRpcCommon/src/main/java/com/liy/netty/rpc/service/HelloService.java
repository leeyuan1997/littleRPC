package com.liy.netty.rpc.service;

public interface HelloService {

    public int add(int a,int b);

    public int add(int a,int b, int c);

    public int add(int ...a);

    public int sub(int a, int b);
}
