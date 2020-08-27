package com.zz.nio.client;

import com.echo.util.InputUtil;
import com.echo.util.SocketInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    public static void main(String[] args) throws IOException {
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(SocketInfo.HOST_NAME, SocketInfo.PORT));
        ByteBuffer buf = ByteBuffer.allocate(40);
        boolean flag = true;
        while(flag){
            buf.clear();
            String inputData = InputUtil.getString("请输入内容");
            buf.put(inputData.getBytes());
            buf.flip();
            clientChannel.write(buf);
            buf.clear();
            int readCount = clientChannel.read(buf);
            buf.flip();
            if(readCount != -1){
                String echoData = new String(buf.array(), 0, readCount).trim();
                System.out.println(echoData);
            }
            if("bye".equalsIgnoreCase(inputData)){
                flag = false;
            }
        }
        clientChannel.close();
    }
}
