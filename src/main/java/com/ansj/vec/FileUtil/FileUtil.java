package com.ansj.vec.FileUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by leoz on 2017/4/9.
 */
public class FileUtil {
    public static void writeStringToFile(String path, String content, boolean append) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(path, append);
        } catch (IOException e) {
            System.out.println("写文件目录出错");
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(writer);
        try {
            bw.write(content);
        } catch (IOException e) {
            System.out.println("写文件出错");
            e.printStackTrace();
        }
        try {
            bw.close();
            writer.close();
        } catch (IOException e) {
            System.out.println("关闭写文件流出错");
            e.printStackTrace();
        }
    }
}
