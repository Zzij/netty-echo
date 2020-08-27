package com.zz.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

/**
 * ChannelInboundHandlerAdapter  针对数据输入的处理
 */

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //当客户端连接成功之后会进行此方法调用
        byte[] data = "服务器连接成功。".getBytes();
        //NIO基于缓存的操作，netty也提供一系列的缓存类（封装NIO的buffer）
        ByteBuf buffer = Unpooled.buffer(data.length);   //Netty自己封装的bytebuff
        buffer.writeBytes(data);   //将数据写入缓存中
        ctx.writeAndFlush(buffer);      // 强制发送所有数据

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try{
            //表示进行数据信息的读取操作，对于读取操作完成后也可以直接回应
            ByteBuf buf = (ByteBuf) msg;  //默认是Bytebuf
            String inputData = buf.toString(CharsetUtil.UTF_8);   //转为字符串，指定编码
            String echoData = "echo: " + inputData;
            if("exit".equalsIgnoreCase(inputData)){
                echoData = "EXIT!!!";
            }
            byte[] echoDataBytes = echoData.getBytes();
            ByteBuf buffer = Unpooled.buffer(echoDataBytes.length);
            buffer.writeBytes(echoDataBytes);
            ctx.writeAndFlush(buffer);
        }finally {
            ReferenceCountUtil.release(msg);   //释放缓存  即使异常也清空
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //捕获异常
        cause.printStackTrace();
        ctx.close();
    }
}
