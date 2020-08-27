package com.zz.chaobai.client;

import com.echo.util.SocketInfo;
import com.zz.chaobai.client.handler.ChaiBaoClientHandler;
import com.zz.chaobai.serious.MessagePackDecode;
import com.zz.chaobai.serious.MessagePackEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.*;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ChaiBaoClient {

    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap client = new Bootstrap();
            client.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {
                    /*ch.pipeline().addLast(new FixedLengthFrameDecoder(1024));//传输字节大小
                    //ch.pipeline().addLast(new LineBasedFrameDecoder(100));
                    ByteBuf delimiter = Unpooled.copiedBuffer(SocketInfo.SEPARATOR.getBytes());  //自定义分隔符
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                    ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));//字符串解码
                    ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));//字符串编码*/


                    /*ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));  //类写入读出
                    ch.pipeline().addLast(new ObjectEncoder());*/
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));
                    ch.pipeline().addLast(new MessagePackDecode());
                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new MessagePackEncode());
                    ch.pipeline().addLast(new ChaiBaoClientHandler());
                }
            });
            ChannelFuture channelFuture = client.connect(SocketInfo.HOST_NAME, SocketInfo.PORT).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

        // 1、如果现在客户端不同，那么也可以不使用多线程模式来处理;
        // 在Netty中考虑到代码的统一性，也允许你在客户端设置线程池
        /*EventLoopGroup group = new NioEventLoopGroup(); // 创建一个线程池
        try {
            Bootstrap client = new Bootstrap(); // 创建客户端处理程序
            client.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true) // 允许接收大块的返回数据
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024)) ;
                            socketChannel.pipeline().addLast(new ChaiBaoClientHandler()); // 追加了处理器
                        }
                    });
            ChannelFuture channelFuture = client.connect(SocketInfo.HOST_NAME, SocketInfo.PORT).sync();
            channelFuture.channel().closeFuture().sync() ; // 关闭连接
        } finally {
            group.shutdownGracefully();
        }*/
    }
}
