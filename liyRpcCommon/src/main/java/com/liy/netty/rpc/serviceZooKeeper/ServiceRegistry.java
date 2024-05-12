package com.liy.netty.rpc.serviceZooKeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class ServiceRegistry {
    private final CuratorFramework curatorClient;

    public ServiceRegistry(String zookeeperConnectionString) {
        curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
    }

    public void registerService(String serviceName, String serviceAddress)  {
        try {
            String path = "/services/" + serviceName + "/providers";
            curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path+"/"+serviceAddress);
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("注册成功");
    }

    public void unregisterService(String serviceName) throws Exception {
        try {
            String path = "/services/" + serviceName;
            curatorClient.delete().deletingChildrenIfNeeded().forPath(path);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
