package com.zz.bio.server;

import com.echo.util.SocketInfo;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(SocketInfo.PORT);
        System.out.println("服务端打开，开启端口在" + SocketInfo.PORT);
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        boolean flag = true;
        while (flag) {
            Socket client = serverSocket.accept();
            executorService.submit(new BIOClientHandler(client));
        }
        executorService.shutdown();
        serverSocket.close();
    }

    private static  class BIOClientHandler implements Runnable{

        private Socket client;

        private PrintStream out;

        private Scanner scanner;

        private boolean flag;

        public BIOClientHandler(Socket client){
            this.client = client;
            this.flag = true;
            try {
                this.out = new PrintStream(this.client.getOutputStream());
                this.scanner = new Scanner(this.client.getInputStream());
                this.scanner.useDelimiter("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while(this.flag){
                if(this.scanner.hasNext()){
                    String val = this.scanner.next().trim();
                    if("bye".equals(val)){
                        this.out.println("BYE...");
                        this.flag = false;
                    }
                    this.out.println("ECHO:" + val);
                }
            }
            this.out.close();
            this.scanner.close();
            try {
                this.client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
