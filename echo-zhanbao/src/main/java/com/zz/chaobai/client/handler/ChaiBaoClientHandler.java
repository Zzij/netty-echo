package com.zz.chaobai.client.handler;

import com.echo.util.InputUtil;
import com.echo.util.SocketInfo;
import com.zz.chaobai.vo.Member;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ChaiBaoClientHandler extends ChannelInboundHandlerAdapter {

    private static int REPEAT = 200;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < REPEAT; i++) {
            //String str = "cong" + i + "times" + System.getProperty("line.separator"); //系统换行符
            //String str = "cong" + i + "times" + SocketInfo.SEPARATOR;    //自定义分隔符  拆包粘包使用
            Member member = new Member(12, "zzj", 80.00);

            ctx.writeAndFlush(member);
        }
        /*for (int x = 0; x < REPEAT; x++) {  // 消息重复发送
            String hello = "【" + x + "】Hello World" + System.getProperty("line.separator");
            byte data [] = hello.getBytes() ;
            System.out.println("客户端：" + hello);
            ByteBuf buf = Unpooled.buffer(data.length) ;
            buf.writeBytes(data) ;
            ctx.writeAndFlush(buf);
        }*/
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try{
            Member member = (Member) msg;
            System.out.println(member);
        }finally {
            ReferenceCountUtil.release(msg);
        }

        // 只要服务器端发送完成信息之后，都会执行此方法进行内容的输出操作
        /*try {
            ByteBuf readBuf = (ByteBuf) msg;
            String readData = readBuf.toString(CharsetUtil.UTF_8).trim(); // 接收返回数据内容
            System.out.println("服务器响应：" + readData); // 输出服务器端的响应内容
        } finally {
            ReferenceCountUtil.release(msg); // 释放缓存
        }*/

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
