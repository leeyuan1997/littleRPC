package com.liy.netty.rpc.serviceZooKeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

public class ServiceDiscovery {
    private final CuratorFramework curatorClient;

    public ServiceDiscovery(String zookeeperConnectionString) {
        curatorClient = CuratorFrameworkFactory.newClient(zookeeperConnectionString, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
    }

    public List<String> discoverServices(String serviceName) throws Exception {
        String path = "/services/" + serviceName + "/providers";
        List<String> serviceAddresses = new ArrayList<>();
        List<String> nodes = curatorClient.getChildren().forPath(path);
//        for (String node : nodes) {
//            byte[] data = curatorClient.getData().forPath(path + "/" + node);
//            String serviceAddress = new String(data);
//            serviceAddresses.add(serviceAddress);
//        }
        return nodes;
    }
}