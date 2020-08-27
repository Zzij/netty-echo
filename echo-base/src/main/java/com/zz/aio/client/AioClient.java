package com.zz.aio.client;

import com.echo.util.InputUtil;
import com.echo.util.SocketInfo;
import com.zz.aio.server.AioServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

class ClientReadHandler implements CompletionHandler<Integer, ByteBuffer>{

    private AsynchronousSocketChannel clientChannel;

    private CountDownLatch latch;

    public ClientReadHandler(AsynchronousSocketChannel clientChannel, CountDownLatch latch){
        this.clientChannel = clientChannel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        String readMessage = new String(buffer.array(), 0, buffer.remaining());
        System.out.println(readMessage);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.latch.countDown();
    }
}

class ClientWriteHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel clientChannel;

    private CountDownLatch latch;

    public ClientWriteHandler(AsynchronousSocketChannel clientChannel, CountDownLatch latch){
        this.clientChannel = clientChannel;
        this.latch = latch;
    }

    @Override
    public void completed(Integer result, ByteBuffer buf) {
        if(buf.hasRemaining()){
            this.clientChannel.write(buf, buf, this);
        }else{
            ByteBuffer readBuffer = ByteBuffer.allocate(50);
            this.clientChannel.read(readBuffer, readBuffer, new ClientReadHandler(this.clientChannel, this.latch));
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.latch.countDown();
    }
}

class AioClientThread implements Runnable{

    private AsynchronousSocketChannel clientChannel;

    private CountDownLatch latch;

    public AioClientThread() throws IOException {
        this.clientChannel = AsynchronousSocketChannel.open();
        this.clientChannel.connect(new InetSocketAddress(SocketInfo.HOST_NAME, SocketInfo.PORT));
        latch = new CountDownLatch(1);
    }

    @Override
    public void run() {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String message) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put(message.getBytes());
        buf.flip();
        this.clientChannel.write(buf, buf, new ClientWriteHandler(clientChannel, latch));
        if("bye".equalsIgnoreCase(message)){
            this.clientChannel.close();
            return false;
        }
        return true;
    }
}

public class AioClient {
    public static void main(String[] args) throws IOException {
        AioClientThread aioThread = new AioClientThread();
        Thread thread = new Thread(aioThread);
        thread.start();
        while(aioThread.sendMessage(InputUtil.getString("请输入内容"))){

        }
    }
}
