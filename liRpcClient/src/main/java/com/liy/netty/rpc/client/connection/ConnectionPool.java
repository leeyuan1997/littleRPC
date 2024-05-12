package com.liy.netty.rpc.client.connection;

import com.liy.netty.rpc.serviceZooKeeper.ServiceDiscovery;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionPool {
    private final Map<String,List<ConnectionPoolEntry>>pool;
    private ServiceDiscovery discovery;
    private ConnectionFactory factory;
   public ConnectionPool(String zookeeperAddress)  {

       this.pool = new ConcurrentHashMap<>();
       discovery = new ServiceDiscovery(zookeeperAddress);
       factory = new ConnectionFactory();
   }
   public  void addConnection(String ZKserviceAddress,List<String> addresses) {
       pool.computeIfAbsent(ZKserviceAddress,k->new LinkedList<ConnectionPoolEntry>());
       List<ConnectionPoolEntry>poollis = pool.get(ZKserviceAddress);
       for(String serviceAddress: addresses) {
           Channel channel = factory.createChannel(serviceAddress);
           String[] addressParts = serviceAddress.split(":");
           String host = addressParts[0];
           int port = Integer.parseInt(addressParts[1]);
           ConnectionPoolEntry poolEntry = new ConnectionPoolEntry();
           poolEntry.setAlive(true);
           poolEntry.setChannel(channel);
           poolEntry.setHost(host);
           poolEntry.setPort(port);
           poollis.add(poolEntry);
       }
   }
   public Channel getConnection(String serviceName){
       try {
           if(!pool.containsKey(serviceName)){

               List<String> addresses = discovery.discoverServices(serviceName);
               addConnection(serviceName,addresses);
           }
       }catch (Exception e) {
           e.printStackTrace();
           System.out.println("ConnectionPool错了");
       }
      return pool.get(serviceName).get(0).getChannel();
   }
    private static class ConnectionPoolEntry{
        Channel channel;
        String host;
        int port;
        boolean alive;


        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isAlive() {
            return alive;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }

}

