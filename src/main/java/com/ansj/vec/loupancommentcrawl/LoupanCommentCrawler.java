package com.ansj.vec.loupancommentcrawl;

import com.ansj.vec.FileUtil.FileUtil;
import com.google.common.collect.Lists;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by leoz on 2017/4/9.
 */
public class LoupanCommentCrawler {
    public static String loupanCommentPath = "/Users/lvlonglong/hacker2017/others/";


    public void getContent(String loupanGroupIdString, String cityId) {
        int pageIndex = 0;
        String[] loupanArray = loupanGroupIdString.split(",");
        List<String> loupanGroupIdList = Lists.newArrayList();
        for (int i = 0; i < loupanArray.length; i++) {
            loupanGroupIdList.add(loupanArray[i]);
        }

        String urlFormat = "http://house.focus.cn/api/getdianping/?group_id=%s&city_id=%s&page=%s&page_size=100";
        //遍历楼盘id获取楼盘评论内容
        for (String groupId : loupanGroupIdList) {
            System.out.println("cityid:" + cityId + ",楼盘id:" + groupId);
            pageIndex++;
            String currentGroupContent = "";
            int page = 1;
            while (true) {
                String url = String.format(urlFormat, groupId, cityId, page + "");
                String currentPageString = this.getCommentContent(url);
                if (currentPageString.length() == 0) {
                    break;   //推出当前楼盘评论内容的获取
                } else {
                    currentGroupContent += currentPageString;
                }
                page++;
            }
            //写入文件
            FileUtil.writeStringToFile(loupanCommentPath + groupId + ".txt", currentGroupContent, true);
            try {
                System.out.println("crawler have a rest");
                if (pageIndex % 20 == 0) {
                    Thread.sleep(1000 * 10);
                } else {
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                System.out.println("thread sleep error");
                e.printStackTrace();
            }
        }
    }
    public String getCommentContent(String urlPath) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
            urlcon.connect();         //获取连接

            InputStreamReader inputStreamReader = new InputStreamReader(urlcon.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String backContent = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                backContent += line;
            }
            //对返回的内容进行解析
            JSONObject jsStr = JSONObject.fromObject(backContent);
            if (jsStr.get("e").equals("9998") || jsStr.get("m").equals("未找到任何数据")) {
                return "";
            }
            String currentPageString = "";
            JSONObject data = (JSONObject)jsStr.get("data");
            JSONArray commentList = (JSONArray)data.get("commentList");
            for (int i = 0; i < commentList.size(); i++) {
                JSONObject tmp = (JSONObject)commentList.get(i);
                String comment = (String)tmp.get("content");
                currentPageString += comment + "\n";
            }
            bufferedReader.close();
            inputStreamReader.close();

            return currentPageString;
        } catch (Exception e) {
            System.out.println("url 连接出错");
            e.printStackTrace();
            return "";
        }
    }

    public void readGroupIdFile(String filePath, String cityId) {
        try {
            String encoding="GBK";
            File file=new File(filePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                int groupIdIndex = 0;
                String groupContent = "";
                while((lineTxt = bufferedReader.readLine()) != null){
                    groupIdIndex++;
                    groupContent += lineTxt + ",";
                    if (groupIdIndex % 2 == 0) {
                        if (groupContent.length() <= 0) {
                            groupContent = "";
                            continue;
                        }
                        groupContent = groupContent.substring(0, groupContent.length() - 1);
                        this.getContent(groupContent, cityId);
                        groupContent = "";
                    }
                }
                if (groupContent.length() > 0) {
                    groupContent = groupContent.substring(0, groupContent.length() - 1);
                    this.getContent(groupContent, cityId);
                }

                read.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LoupanCommentCrawler loupanCommentCrawler = new LoupanCommentCrawler();
//        loupanCommentCrawler.readGroupIdFile("C:\\Users\\leoz\\Desktop\\hacker2017project\\groupId1.txt");
//        loupanCommentCrawler.readGroupIdFile("/Users/lvlonglong/hacker2017/groupId/others.txt");
        List<Integer> cityIds = Lists.newArrayList(13, 14, 15, 16, 19, 18, 23, 22, 27, 29, 28, 30, 39, 40, 46, 50, 49, 48, 86, 83, 128, 222, 20, 25, 26, 34, 38, 36, 37, 42, 43, 47, 44, 55, 53, 84, 93, 92, 89, 88, 91, 90, 102, 100, 96, 97, 109, 104, 105, 119, 115, 126, 122, 120, 136, 141, 140, 142, 129, 131, 133, 132, 135, 134, 145, 147, 148, 171, 170, 169, 161, 167, 165, 187, 184, 185, 190, 188, 189, 179, 177, 181, 205, 207, 199, 223, 217, 219, 213, 214, 208, 209, 234, 305, 304, 307, 309);
        for (Integer cityid : cityIds) {
            loupanCommentCrawler.readGroupIdFile(String.format("/Users/lvlonglong/hacker2017/groupId/%s.txt", cityid + ""), cityid + "");
        }
    }
}
