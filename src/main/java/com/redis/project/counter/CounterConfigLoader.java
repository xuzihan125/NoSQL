package com.redis.project.counter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.project.util.EnumCamp;
import com.redis.project.util.JedisInstance;
import com.redis.project.util.MyFileFilter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CounterConfigLoader {
    private Map<String,CounterConfig> configMap;
    private Map<String,Class> counterMap;

    public CounterConfigLoader() throws IOException {
        initCounterMap();
        checkFile();
        loadCounterMap();

    }

    private void initCounterMap(){
        counterMap = new HashMap<>();
        counterMap.put("num",NumCounter.class);
        counterMap.put("list",ListCounter.class);
        counterMap.put("op",OpCounter.class);
        counterMap.put("string",StringCounter.class);
        counterMap.put("set",SetCounter.class);
        counterMap.put("zset",ZSetCounter.class);
    }

    private void checkFile(){
        String path = EnumCamp.Directory.COUNTER_CONFIG_PATH.getDir();
        FileFilter filter = FileFilterUtils.and(new MyFileFilter());
        FileAlterationObserver filealtertionObserver=new FileAlterationObserver(path, filter);
        filealtertionObserver.addListener(new FileAlterationListenerAdaptor(){
            @Override
            public void onFileChange(File file)
            {
                try{
                    loadCounterMap();
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
    }

    public void loadCounterMap() {
        File file = new File(EnumCamp.Directory.COUNTER_CONFIG_PATH.getPath());
        try{
            String data = FileUtils.readFileToString(file, "UTF-8");
            JSONObject jsonData = JSONObject.parseObject(data);
            JSONArray jsonArray = jsonData.getJSONArray("counter");
            configMap = new HashMap<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                CounterConfig counter;
                counter=jsonArray.getJSONObject(i).toJavaObject(CounterConfig.class);
                configMap.put(jsonArray.getJSONObject(i).getString("name"),counter);
            }
            JedisPool jedisPool= JedisInstance.getInstance();
            Jedis jedis = jedisPool.getResource();
            new StringCounter(configMap.get("setRoomDesc"),jedis).redisOp();
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public CounterConfig getCounterConfig(String key){
        CounterConfig counterConfig = configMap.get(key);
        if(counterConfig==null){
            throw new RuntimeException("action中的config类型设置错误");
        }
        return counterConfig;
    }

    public Class getCounter(String key){
        Class aClass = counterMap.get(key);
        if(aClass==null){
            throw new RuntimeException("counter中的type类型设置错误");
        }
        return aClass;
    }
}
