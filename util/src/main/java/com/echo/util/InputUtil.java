package com.echo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputUtil {

    private static final BufferedReader keyboard_input = new BufferedReader(new InputStreamReader(System.in));

    private InputUtil(){}


    public static String getString(String prompt) throws IOException {
        System.out.println(prompt);
        boolean flag = true;
        String str = null;
        while(flag){
            str = keyboard_input.readLine();
            if(str == null || "".equals(str)){
                System.out.println("输入内容不允许为空");
            }else{
                flag = false;
            }
        }
        return str;
    }
}
