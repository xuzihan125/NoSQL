package client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.redis.project.util.EnumCamp;
import io.lettuce.core.dynamic.annotation.Value;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class client {
    public static void main(String[] arg){
        System.out.println("欢迎使用学生系统");
        Scanner scanner = new Scanner(System.in);
        Pattern r = Pattern.compile("^\\s*([0-9]|10)\\s*$");
        Map<String,String> actionMap = getActionMap();
        boolean flag = true;
        while(flag) {
            printOperation();
            System.out.print("> ");
            Matcher m = r.matcher(scanner.nextLine());
            if (!m.find()) {
                System.out.print("错误输入!");
                continue;
            }
            JSONObject output = new JSONObject();
            output.put(EnumCamp.Value.TYPE.getCode(),actionMap.get(m.group(1)));
            switch(Integer.parseInt(m.group(1))){
                case 0:
                case 1:
                    System.out.println("请输入用户名（输入cancel取消操作）");
                    String name = scanner.nextLine();
                    if(Pattern.matches("^\\s*cancel\\s*$",name)){
                        continue;
                    }
                    output.put(EnumCamp.Value.NAME.getCode(),name);
                    output.put(EnumCamp.Value.VALUE.getCode(),0);
                    break;
                case 2:
                    System.out.println("请输入用户名（输入cancel取消操作）");
                    String name_1 = scanner.nextLine();
                    if(Pattern.matches("^\\s*cancel\\s*$",name_1)){
                        continue;
                    }
                    output.put(EnumCamp.Value.NAME.getCode(),name_1);
                    output.put(EnumCamp.Value.VALUE.getCode(),0);
                    System.out.println("请输入打赏金额（输入-1取消操作）");
                    Double value = Double.parseDouble(scanner.nextLine());
                    if(value == -1){
                        return;
                    }
                    output.put(EnumCamp.Value.VALUE.getCode(),value);
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    output.put(EnumCamp.Value.NAME.getCode(),"");
                    output.put(EnumCamp.Value.VALUE.getCode(),0);
                    break;
                case 9:
                    flag = false;
                    continue;
                default:
                    System.out.println("选择的操作不存在");
                    continue;
            }

            JSONObject result = connect(output);
            if(result == null || result.get(EnumCamp.Value.STATE.getCode())==null){
                System.out.println("网络连接错误");
                continue;
            }

            System.out.println(result.getString(EnumCamp.Value.STATE.getCode()).equals("success")?"操作成功":"操作失败");
            if(result.get(EnumCamp.Value.FEEDBACK.getCode())!=null){
                System.out.println(result.getString(EnumCamp.Value.FEEDBACK.getCode()));
            }
            if(result.get(EnumCamp.Value.STRINGLIST.getCode())!=null){
                List<String> sl = (List) result.get(EnumCamp.Value.STRINGLIST.getCode());
                for(String s: sl){
                    System.out.println(s);
                }
            }


        }
    }

    public static void printOperation(){
        System.out.println("\nRedis直播间统计系统：");
        System.out.println("0.进入房间");
        System.out.println("1.退出房间");
        System.out.println("2.打赏");
        // str
        System.out.println("3.房间描述");
        // num
        System.out.println("4.总人数统计");
        // FREQ
        System.out.println("5.给定时间内浏览人员");
        // list
        System.out.println("6.打赏历史");
        // zset
        System.out.println("7.打赏排行榜");
        System.out.println("8.清空历史记录");
        System.out.println("9.退出");
    }

    public static Map<String,String> getActionMap(){
        Map<String,String> map = new HashMap<>();
        map.put("0","enter");
        map.put("1","leave");
        map.put("2","reward");
        map.put("3","desc");
        map.put("4","totalPeople");
        map.put("5","FREQ");
        map.put("6","rewardHistory");
        map.put("7","rewardList");
        map.put("8","clean");
        return map;
    }

    public static JSONObject connect(JSONObject obj){
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader reader = null;
        JSONObject result = null;
        try {
            URL url = new URL(EnumCamp.Connection.LOCAL.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            os = connection.getOutputStream();
            os.write(obj.toString().getBytes());

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String receiver ;
            StringBuffer buffer = new StringBuffer();
            while((receiver = reader.readLine())!=null){
                receiver = new String(receiver.getBytes());
                buffer.append(receiver);
            }
            result = (JSONObject) JSON.parse(buffer.toString());
        } catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            connection.disconnect();
        }
        return result;
    }
}
