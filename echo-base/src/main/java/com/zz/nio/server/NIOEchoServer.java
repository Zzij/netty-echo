package com.zz.nio.server;

import com.echo.util.SocketInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NIOEchoServer {

    public static void main(String[] args) throws Exception {
        //1 NIO实现考虑到性能的问题以及响应时间问题
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        //2 NIO的处理是基于Channel控制的，Selector负责管理所有的channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3 设置一个非阻塞的状态机制
        serverSocketChannel.configureBlocking(false);
        //4 服务器上提供有一个监听端口
        serverSocketChannel.bind(new InetSocketAddress(SocketInfo.PORT));
        //5 设置一个selector，作为一个选择器的出现，目的是管理所有的channel
        Selector selector = Selector.open();
        //6 将当前的Channel注册到selector中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT); //连接时处理
        System.out.println("服务器已经成功启动，服务器的监听的端口为：" + SocketInfo.PORT);
        //7 NIO采取轮询模式，每当有用户连接的时候，就启动一个线程（线程池管理）
        int keySelect = 0;
        while((keySelect = selector.select()) > 0){
            Set<SelectionKey> selectionKeys = selector.selectedKeys();    //获取全部的key
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
            while(selectionKeyIterator.hasNext()){
                SelectionKey selectionKey = selectionKeyIterator.next();
                try{
                    if(selectionKey.isAcceptable()){   //为连接模式
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        if(clientChannel != null){
                            executorService.submit(new EchoNIOHandler(clientChannel));
                        }
                    }
                    selectionKeyIterator.remove();
                }catch (Exception e){
                    selectionKey.cancel();
                    e.printStackTrace();
                }
            }
        }
        executorService.shutdown();
        serverSocketChannel.close();
    }

    private static class EchoNIOHandler implements Runnable{

        private SocketChannel clientChannel;
        private boolean flag;
        public EchoNIOHandler(SocketChannel clientChannel){
            this.clientChannel = clientChannel;
            this.flag = true;
        }

        @Override
        public void run() {
            //40个缓冲区
            ByteBuffer buf = ByteBuffer.allocate(40);

            while(this.flag){
                buf.clear(); //清空缓冲区
                try {
                    int readCount = this.clientChannel.read(buf);
                    String readMessage = new String(buf.array(), 0, readCount).trim();
                    String writeMessage = "ECHO :" + readMessage;
                    if("bye".equalsIgnoreCase(readMessage)){
                        writeMessage = "EXIT bye";
                        this.flag = false;
                    }
                    buf.clear();
                    buf.put(writeMessage.getBytes());
                    buf.flip();
                    this.clientChannel.write(buf);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            try {
                clientChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
