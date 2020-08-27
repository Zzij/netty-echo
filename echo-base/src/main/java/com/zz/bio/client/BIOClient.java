package com.zz.bio.client;

import com.echo.util.InputUtil;
import com.echo.util.SocketInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class BIOClient {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket(SocketInfo.HOST_NAME, SocketInfo.PORT);
        client.setKeepAlive(true);
        client.setSoTimeout(1000);
        Scanner scanner = new Scanner(client.getInputStream());
        PrintStream out = new PrintStream(client.getOutputStream());
        boolean flag = true;
        while(flag){
            String data = InputUtil.getString("请输入消息:");
            out.println(data);
            if(scanner.hasNext()){
                String str = scanner.next();
                System.out.println(str);
            }
            if("bye".equalsIgnoreCase(data)){
                flag = false;
            }
        }
        client.close();
    }
}
