package test;

import com.ansj.vec.preprocess.ReduceFile;
import com.google.common.collect.Lists;
import com.sun.deploy.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by leoz on 2017/4/3.
 */
public class PreProcess {
    /**
     *取部分文件
     * */
    public void getSomeData() {
        try {
            StringBuffer sb = new StringBuffer("");

            FileReader reader = new FileReader("C:\\Users\\leoz\\Desktop\\hacker2017project\\info\\wiki_chinese_preprocessed.simplied.txt");
//            FileReader reader = new FileReader("C:\\Users\\leoz\\Desktop\\hacker2017project\\info\\wordvector.txt");
            BufferedReader br = new BufferedReader(reader);

            String str = null;

            int index = 0;
            // write string to file
            FileWriter writer = new FileWriter("C:\\Users\\leoz\\Desktop\\hacker2017project\\info\\predata_1.txt", true);
            BufferedWriter bw = new BufferedWriter(writer);


            while ((str = br.readLine()) != null) {
                index++;
                String[] strList = str.split(" ");
                List<String> resultList = Lists.newArrayList();
                for (int i = 0; i < strList.length; i++) {
                    if (!ReduceFile.stop_words.contains(strList[i])) {
                        resultList.add(strList[i]);
                    }
                }

                String result = StringUtils.join(resultList, " ");
                sb.append(result + "\n");
//                sb.append(str + "/n");
                System.out.println(result);
//                if (index % 100 == 0) {
//                    index = 0;

                    bw.write(sb.toString());
                    sb = new StringBuffer("");
//                }
            }

            br.close();
            reader.close();

            bw.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        ReduceFile reduceFile = new ReduceFile();
        reduceFile.readStopWords(ReduceFile.stop_words_path);
        PreProcess preProcess = new PreProcess();
        preProcess.getSomeData();
    }
}
