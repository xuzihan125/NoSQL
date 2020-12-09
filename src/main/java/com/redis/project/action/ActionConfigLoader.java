package com.redis.project.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.project.counter.BaseCounter;
import com.redis.project.counter.CounterConfig;
import com.redis.project.counter.CounterConfigLoader;
import com.redis.project.util.EnumCamp;
import com.redis.project.util.JedisInstance;
import com.redis.project.util.MyFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ActionConfigLoader {
    private Map<String, ActionConfig> actionMap;

    public ActionConfigLoader() {
        String path = EnumCamp.Directory.ACTION_CONFIG_PATH.getDir();
        FileFilter filter = FileFilterUtils.and(new MyFileFilter());
        FileAlterationObserver filealtertionObserver=new FileAlterationObserver(path, filter);
        filealtertionObserver.addListener(new FileAlterationListenerAdaptor(){
            @Override
            public void onFileChange(File file)
            {
                try{
                    loadActionMap();
                }catch(Exception e) {
                    e.printStackTrace();
                }
                super.onFileChange(file);
            }
        });
        FileAlterationMonitor filealterationMonitor=new FileAlterationMonitor();
        filealterationMonitor.addObserver(filealtertionObserver);
        try{
            filealterationMonitor.start();
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            loadActionMap();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadActionMap() throws IOException{
        File file = new File(EnumCamp.Directory.ACTION_CONFIG_PATH.getPath());
        String data = FileUtils.readFileToString(file, "UTF-8");
        JSONObject jsonData = JSONObject.parseObject(data);
        JSONArray jsonArray = jsonData.getJSONArray("action");
        actionMap = new HashMap<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            ActionConfig counter;
            counter=jsonArray.getJSONObject(i).toJavaObject(ActionConfig.class);
            actionMap.put(jsonArray.getJSONObject(i).getString("name"),counter);
        }
    }

    public ActionConfig getAction(String key){
        ActionConfig actionConfig = actionMap.get(key);
        if(actionConfig == null){
            throw new RuntimeException("输入action类型错误");
        }
        return actionConfig;
    }

}
