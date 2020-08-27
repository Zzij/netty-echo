package com.zz.aio.server;

import com.echo.util.SocketInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;


class EchoHandler implements CompletionHandler<Integer, ByteBuffer> {

    private AsynchronousSocketChannel clientChannel;

    private boolean exit;  //是否结束

    public EchoHandler(AsynchronousSocketChannel clientChannel){
        this.clientChannel = clientChannel;
        exit = false;
    }

    @Override
    public void completed(Integer result, ByteBuffer buf) {
        buf.flip();
        String readMessage = new String(buf.array(), 0, buf.remaining());
        String echoMessage = "ECHO:" + readMessage;
        if("bye".equalsIgnoreCase(readMessage)){
            echoMessage = "EXIT bye...";
            this.exit = true;
        }
        this.echoWrite(echoMessage);
    }

    private void echoWrite(String message){
        ByteBuffer buf = ByteBuffer.allocate(50);
        buf.put(message.getBytes());
        buf.flip();
        this.clientChannel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                if(buffer.hasRemaining()){ //缓存中是否有数据
                    EchoHandler.this.clientChannel.write(buffer, buffer, this);
                }else {
                    if(EchoHandler.this.exit == false){//还未结束
                        ByteBuffer buffer1 = ByteBuffer.allocate(50);
                        EchoHandler.this.clientChannel.read(buffer1, buffer1, new EchoHandler(EchoHandler.this.clientChannel));
                    }
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    EchoHandler.this.clientChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//连接接受的回调处理操作
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServerThread> {

    @Override
    public void completed(AsynchronousSocketChannel channel, AioServerThread aioThread) {
        aioThread.getServerSocketChannel().accept(aioThread, this);  //接受连接
        ByteBuffer buf = ByteBuffer.allocate(50);
        channel.read(buf, buf, new EchoHandler(channel));

    }

    @Override
    public void failed(Throwable exc, AioServerThread aioThread) {
        System.err.println("客户端连接创建失败。。。。。");
        aioThread.getLatch().countDown();
    }
}


class AioServerThread implements Runnable{
    private AsynchronousServerSocketChannel serverSocketChannel;   //服务器通道

    private CountDownLatch latch;     //做同步处理操作
    public AioServerThread() throws IOException {
        latch = new CountDownLatch(1);  //等待线程数量为1
        //打开通道
        serverSocketChannel = AsynchronousServerSocketChannel.open();
        //设置端口地址
        this.serverSocketChannel.bind(new InetSocketAddress(SocketInfo.PORT));
        System.out.println("服务开启，端口为" + SocketInfo.PORT);
    }

    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    @Override
    public void run() {
        this.serverSocketChannel.accept(this, new AcceptHandler());

        try {
            this.latch.await();//线程等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class AioServer {

    public static void main(String[] args) throws IOException {
        new Thread(new AioServerThread()).start();
    }

}
