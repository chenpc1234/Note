package com.crazymakercircle.Bootstrap;

import com.crazymakercircle.netty.http.echo.HttpEchoServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


//自动加载配置信息
//@Configuration
//使包路径下带有@Value的注解自动注入
//使包路径下带有@Autowired的类可以自动注入
@ComponentScan("com.crazymakercircle.netty.http")
@SpringBootApplication
public class EchoServerBootstrapApplication
{

    /**入口方法
     * @param args
     */
    public static void main(String[] args) {
        // 启动并初始化 Spring 环境及其各 Spring 组件
        ApplicationContext context =
                SpringApplication.run(EchoServerBootstrapApplication.class, args);
        try
        {
            /**
             * 启动Netty 版Http Echo Server
             */
            HttpEchoServer.start();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

}
