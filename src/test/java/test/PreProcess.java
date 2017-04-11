package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ansj.vec.preprocess.ReduceFile;
import com.google.common.collect.Lists;

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

            FileReader reader = new FileReader("/Users/lvlonglong/hacker2017/wiki_chinese/segment_data/wiki_chinese_preprocessed.simplied.txt");
//            FileReader reader = new FileReader("C:\\Users\\leoz\\Desktop\\hacker2017project\\info\\wordvector.txt");
            BufferedReader br = new BufferedReader(reader);

            String str = null;

            int index = 0;
            // write string to file
            FileWriter writer = new FileWriter("/Users/lvlonglong/hacker2017/wiki_chinese/predata/predata_7.txt", true);
            BufferedWriter bw = new BufferedWriter(writer);


//            while ((str = br.readLine()) != null) {
//                index++;
//                sb.append(str + "/n");
//                System.out.println(str);
//                if (index % 100 == 0) {
//                    index = 0;
//
//                    bw.write(sb.toString());
//                }
//            }

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
//                System.out.println(result);

                bw.write(sb.toString());
                bw.flush();
                sb = new StringBuffer("");
                System.out.println(index);
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
        reduceFile.readStopWords("/Users/lvlonglong/hacker2017/stopwords.txt");

        PreProcess preProcess = new PreProcess();
        preProcess.getSomeData();
    }
}
