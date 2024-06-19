package com.libowen.util;

import java.io.IOException;

/**
 * @Author: libw1
 * @Date: 2024/04/02/16:33
 * @Description:
 */
public class ScreenUtil {
    public static void clearScreen() {
        try{
            if(System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            }else {
                new ProcessBuilder("bash","-c","clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
