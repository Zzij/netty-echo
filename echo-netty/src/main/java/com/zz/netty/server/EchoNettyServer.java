package com.zz.netty.server;

import com.echo.util.SocketInfo;
import com.zz.netty.server.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

//实现基础线程池与网络连接配置
public class EchoNettyServer {

    //服务器端启动处理
    public void run() throws InterruptedException {
        //线程池提升服务器性能，利用定长的线程池可以保证核心线程数量
        //netty线程池分为2类：主线程池（接受客户端连接）、工作线程池（处理客户端连接）

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(5);   //创建主线程
        NioEventLoopGroup workGroup = new NioEventLoopGroup(5);   //创建工作线程
        try{
            //创建服务器端的程序类进行NIO启动，同时设置channel
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //设置当前的线程池和channel类型
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
            //收到消息需要处理，定义字处理器
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new EchoServerHandler());   //追加处理器
                }
            });
            //可以直接利用常量进行TCP协议相关配置
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //ChannelFuture表示异步回调的操作
            ChannelFuture channelFuture = serverBootstrap.bind(SocketInfo.PORT).sync();
            System.out.println("服务端打开成功，端口在" + SocketInfo.PORT);
            channelFuture.channel().closeFuture().sync();   //等待socket被关闭
        }finally {
            //关闭线程
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
