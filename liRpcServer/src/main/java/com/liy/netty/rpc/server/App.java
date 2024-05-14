package com.liy.netty.rpc.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@ComponentScan(value = "com.liy.netty.rpc.server")
@Configuration
@PropertySource(value = "classpath:server.properties")
public class App {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(App.class);


        LiRpcServer server = context.getBean(LiRpcServer.class);
        server.run();

    }
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


}
