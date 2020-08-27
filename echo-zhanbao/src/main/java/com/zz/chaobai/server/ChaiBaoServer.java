package com.zz.chaobai.server;

import com.echo.util.SocketInfo;

import com.zz.chaobai.client.handler.ChaiBaoClientHandler;
import com.zz.chaobai.serious.MessagePackDecode;
import com.zz.chaobai.serious.MessagePackEncode;
import com.zz.chaobai.server.handler.ChaiBaoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ChaiBaoServer {

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(10);
        EventLoopGroup workGroup = new NioEventLoopGroup(20);
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel channel) throws Exception {
                    /*channel.pipeline().addLast(new FixedLengthFrameDecoder(100));//传输字节大小
                    //channel.pipeline().addLast(new LineBasedFrameDecoder(1024));   //拆包与粘包器
                    ByteBuf delimiter = Unpooled.copiedBuffer(SocketInfo.SEPARATOR.getBytes());
                    channel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                    channel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));//字符串解码
                    channel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));//字符串编码*/
                    /*channel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                    channel.pipeline().addLast(new ObjectEncoder());*/
                    channel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));
                    channel.pipeline().addLast(new MessagePackDecode());
                    channel.pipeline().addLast(new LengthFieldPrepender(4));
                    channel.pipeline().addLast(new MessagePackEncode());
                    channel.pipeline().addLast(new ChaiBaoServerHandler());
                }
            });
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(SocketInfo.PORT).sync();
            System.out.println("服务器 启动成功地址在 " + SocketInfo.PORT);
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

        // 线程池是提升服务器性能的重要技术手段，利用定长的线程池可以保证核心线程的有效数量
        // 在Netty之中线程池的实现分为两类：主线程池（接收客户端连接）、工作线程池（处理客户端连接）
        /*EventLoopGroup bossGroup = new NioEventLoopGroup(10); // 创建接收线程池
        EventLoopGroup workerGroup = new NioEventLoopGroup(20); // 创建工作线程池
        System.out.println("服务器启动成功，监听端口为：" + SocketInfo.PORT);
        try {
            // 创建一个服务器端的程序类进行NIO启动，同时可以设置Channel
            ServerBootstrap serverBootstrap = new ServerBootstrap();   // 服务器端
            // 设置要使用的线程池以及当前的Channel类型
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            // 接收到信息之后需要进行处理，于是定义子处理器
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024)) ;
                    socketChannel.pipeline().addLast(new ChaiBaoServerHandler()); // 追加了处理器
                }
            });
            // 可以直接利用常亮进行TCP协议的相关配置
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            // ChannelFuture描述的时异步回调的处理操作
            ChannelFuture future = serverBootstrap.bind(SocketInfo.PORT).sync();
            future.channel().closeFuture().sync();// 等待Socket被关闭
        } finally {
            workerGroup.shutdownGracefully() ;
            bossGroup.shutdownGracefully() ;
        }*/
    }
}
