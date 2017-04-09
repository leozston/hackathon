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
    public static String loupanCommentPath = "C:\\Users\\leoz\\Desktop\\hacker2017project\\loupancomment\\";


    public void getContent(String loupanGroupIdString) {
        int pageIndex = 0;
        String[] loupanArray = loupanGroupIdString.split(",");
        List<String> loupanGroupIdList = Lists.newArrayList();
        for (int i = 0; i < loupanArray.length; i++) {
            loupanGroupIdList.add(loupanArray[i]);
        }

        String urlFormat = "http://house.focus.cn/api/getdianping/?group_id=%s&city_id=1&page=%s&page_size=10";
        //遍历楼盘id获取楼盘评论内容
        for (String groupId : loupanGroupIdList) {
            System.out.println("楼盘id:" + groupId);
            pageIndex++;
            String currentGroupContent = "";
            int page = 1;
            while (true) {
                String url = String.format(urlFormat, groupId, page + "");
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
                    Thread.sleep(1000 * 60);
                } else {
                    Thread.sleep(1000 * 2);
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
//                System.out.println(line);
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
//                System.out.println(comment);
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

    public void readGroupIdFile(String filePath) {
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
                        this.getContent(groupContent);
                        groupContent = "";
                    }
                }
                if (groupContent.length() > 0) {
                    groupContent = groupContent.substring(0, groupContent.length() - 1);
                    this.getContent(groupContent);
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
        loupanCommentCrawler.readGroupIdFile("C:\\Users\\leoz\\Desktop\\hacker2017project\\groupId1.txt");
    }
}
