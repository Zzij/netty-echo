package com.zz.netty.client.handler;

import com.echo.util.InputUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //服务器完成发送消息，执行此方法 每次
        try {
            ByteBuf buf = (ByteBuf) msg;
            String readMessage = buf.toString(CharsetUtil.UTF_8).trim();
            if("EXIT!!!".equalsIgnoreCase(readMessage)){
                System.out.println("EXIT  退出会话");
                ctx.close();
            }else{
                System.out.println(readMessage);
                String inputData = InputUtil.getString("请输入消息：");
                byte[] inputDataBytes = inputData.getBytes();
                ByteBuf buffer = Unpooled.buffer(inputDataBytes.length);
                buffer.writeBytes(inputDataBytes);
                ctx.writeAndFlush(buffer);
            }
        }finally {
            //释放缓存
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
