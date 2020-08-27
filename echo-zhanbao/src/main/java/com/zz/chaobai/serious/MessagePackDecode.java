package com.zz.chaobai.serious;

import com.zz.chaobai.vo.Member;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.Templates;

import java.nio.ByteBuffer;
import java.util.List;

public class MessagePackDecode extends MessageToMessageDecoder<ByteBuf> {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readableBytes();
        byte[] read = new byte[len];
        byteBuf.getBytes(byteBuf.readerIndex(), read, 0, len);   //读取长度
        MessagePack messagePack = new MessagePack();
        //list.add(messagePack.read(read));
        list.add(messagePack.read(read, messagePack.lookup(Member.class)));
    }
}
