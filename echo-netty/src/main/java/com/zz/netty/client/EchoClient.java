package com.zz.netty.client;


import com.echo.util.SocketInfo;
import com.zz.netty.client.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class EchoClient {

    public void run() throws InterruptedException {
        //1.客户端不同，可以不使用多线程
        //netty考虑代码统一性  允许在客户端设置线程池
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class)
                                .option(ChannelOption.TCP_NODELAY,true)    //允许接受大块数据
                                .handler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                                        socketChannel.pipeline().addLast(new EchoClientHandler());
                                    }
                                });
            ChannelFuture channelFuture = client.connect(SocketInfo.HOST_NAME, SocketInfo.PORT).sync();
            //监听回调 是否成功连接
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("连接服务器成功，现在可以进行信息传输");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();//等待关闭连接
        }finally {
            group.shutdownGracefully();
        }
    }
}
