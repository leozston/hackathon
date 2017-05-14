package com.ansj.vec.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.huaban.analysis.jieba.JiebaSegmenter;

/**
 * Created by lvlonglong on 2017/4/7.
 */
public class ReduceFile {
//    private static String focus_data_pre = "/Users/lvlonglong/hacker2017/test";   //数据预处理之前路径
    private static String focus_data_pre = "/Users/liweipeng/myProject/hackathon/predata";   //数据预处理之前路径
    private static String focus_data_reduce = "/Users/liweipeng/myProject/hackathon/reducedata/reduce_data.txt";   //数据预处理之后路径
    public static String stop_words_path = "C:\\Users\\leoz\\Desktop\\hacker2017project\\stopwords.txt";
    public static List<String> stop_words = Lists.newArrayList();

    public void readStopWords(String path) {
        FileReader reader = null;
        JiebaSegmenter segmenter = new JiebaSegmenter();
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            System.out.println("***********文件路径错误*********");
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                stop_words.add(line);
            }
        } catch (IOException e) {
            System.out.println("***********读取文件失败************");
            e.printStackTrace();
        }
        try {
            br.close();
            reader.close();
        } catch (IOException e) {
            System.out.println("***********关闭读文件流失败************");
            e.printStackTrace();
        }
    }


    /**
     * 数据预处理函数，将一些数据剔除掉
     * */
    public void dataPreProcess(String path) {
        File file=new File(path);
        File[] tempList = file.listFiles();
        System.out.println("预处理之前目录下文件个数："+tempList.length);
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                if (tempList[i].toString().contains(".DS_Store")) {
                    continue;
                }
                dataPreProcessing(tempList[i].toString(), i);
                System.out.println(tempList[i].toString());
                System.out.println("第" + i + "个已处理完成");
            } else {
                dataPreProcess(tempList[i].getAbsolutePath());
            }
        }
    }

    public void  dataPreProcessing(String fromPath, int index) {
        FileReader reader = null;
        JiebaSegmenter segmenter = new JiebaSegmenter();
        try {
            reader = new FileReader(fromPath);
        } catch (FileNotFoundException e) {
            System.out.println("***********文件路径错误*********");
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        String fileContent = "";
        try {
            while ((line = br.readLine()) != null) {
                List<String> currentLineWords = segmenter.sentenceProcess(line);
                List<String> currentLineUnStopsWords = Lists.newArrayList();
                for (String s : currentLineWords) {
                    if (!stop_words.contains(s)) {
                        currentLineUnStopsWords.add(s);
                    }
                }
                line = StringUtils.join(currentLineUnStopsWords, " ");
                fileContent += line + "\n";
            }
        } catch (IOException e) {
            System.out.println("***********读取文件失败************");
            e.printStackTrace();
        }
        try {
            br.close();
            reader.close();
        } catch (IOException e) {
            System.out.println("***********关闭读文件流失败************");
            e.printStackTrace();
        }

        //写文件
        FileWriter writer = null;
        try {
            writer = new FileWriter(focus_data_reduce, true);
        } catch (IOException e) {
            System.out.println("写文件目录出错");
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(writer);
        try {
            bw.write(fileContent);
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


    public static void main(String[] args) {
        ReduceFile reduceFile = new ReduceFile();
        reduceFile.readStopWords(stop_words_path);
        reduceFile.dataPreProcess(focus_data_pre);
    }
}
