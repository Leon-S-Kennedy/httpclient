package com.libowen;

import com.libowen.util.HttpUtil;
import com.libowen.util.RequestUtil;
import com.libowen.util.ScreenUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @Author: libw1
 * @Date: 2024/04/02/16:04
 * @Description:
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        if(args.length==0||"repeat".equalsIgnoreCase(args[0])){
            if(args.length<2){
                Scanner scanner = new Scanner(System.in);
                while (true){
                    System.out.println("请输入请求文件路径(输入'quit'退出、'clear'清屏)");
                    String line = scanner.nextLine();
                    if("quit".equalsIgnoreCase(line.trim())){
                        break;
                    }else if("clear".equalsIgnoreCase(line.trim())){
                        ScreenUtil.clearScreen();
                    }else {
                        RequestUtil.sendAndPrint(line.trim());
                    }
                }
                scanner.close();
                HttpUtil.close();
            }else {
                log.info("参数过多");
            }
        }else if("single".equalsIgnoreCase(args[0])){
            if(args.length==2){
                String requestFilePath=args[1];
                RequestUtil.sendAndPrint(requestFilePath);
                HttpUtil.close();
            }else {
                log.info("缺少路径参数或者参数过多");
            }
        }else {
            log.info("不支持{}模式",args[0]);
        }
    }
}
