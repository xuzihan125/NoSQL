//package com.redis.project.util;
//
//import com.alibaba.fastjson.JSONObject;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.filefilter.FileFilterUtils;
//import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
//import org.apache.commons.io.monitor.FileAlterationMonitor;
//import org.apache.commons.io.monitor.FileAlterationObserver;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPool;
//
//
//import java.io.File;
//import java.io.FileFilter;
//import java.sql.Timestamp;
//import java.util.*;
//
//public class RedisOperation {
//    private JedisPool jedisPool;
//    private long incrNum;
//    private String incrKey;
//    private String descKey;
////    private String setKey;
//    private String userZsetKey;
//    private String rewardZsetKey;
//    private String listKey;
//    private long beginTime;
//    private long endtime;
//    private long endRange;
//
//
//    /**
//     * @Author:徐子涵
//     * @Date:2020-12-02
//     * @desc:参数初始化，创建文件监听
//     */
//    public RedisOperation(){
//        String path = EnumCamp.Directory.JSON_PATH.getDir();
//        FileFilter filter = FileFilterUtils.and(new MyFileFilter());
//        FileAlterationObserver filealtertionObserver=new FileAlterationObserver(path, filter);
//        filealtertionObserver.addListener(new FileAlterationListenerAdaptor(){
//            @Override
//            public void onFileChange(File file)
//            {
//                loadParam();
//                super.onFileChange(file);
//            }
//        });
//
//        FileAlterationMonitor filealterationMonitor=new FileAlterationMonitor();
//        filealterationMonitor.addObserver(filealtertionObserver);
//        try{
//            filealterationMonitor.start();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        jedisPool = JedisInstance.getInstance();
//        loadParam();
//    }
//
//    /**
//     * @Author:徐子涵
//     * @Date:2020-12-02
//     * @desc:从json文件中加载参数数据
//     */
//    public void loadParam(){
//        try{
//            Jedis jedis = jedisPool.getResource();
//            File file = new File(EnumCamp.Directory.JSON_PATH.getPath());
//            String data = FileUtils.readFileToString(file, "UTF-8");
//            JSONObject jsonData = JSONObject.parseObject(data);
//            incrNum = (int) jsonData.get("increase_num");
//            incrKey = (String) jsonData.get("keyField");
//            descKey = (String) jsonData.get("descKey");
//            jedis.set(descKey,(String) jsonData.get("desc"));
//            setKey = (String) jsonData.get("setKey");
//            userZsetKey = (String) jsonData.get("userZsetKey");
//            rewardZsetKey = (String) jsonData.get("rewardZsetKey");
//            listKey = (String) jsonData.get("listKey");
//            String endTimeString = (String) jsonData.get("endTime");
//            endtime = endTimeString.equals("currentDate")?new Date().getTime():Timestamp.valueOf((String) jsonData.get("beginTime")).getTime();
//            beginTime = endtime-(int) jsonData.get("timeLength");
//            endRange = (int) jsonData.get("endRange");
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
////    /**
////     * @Author:徐子涵
////     * @Date:2020-12-02
////     * @desc:检验用户是否在房间中
////     */
////    public boolean check(String userName){
////        Jedis jedis = jedisPool.getResource();
////        boolean result;
////        try{
////            result = jedis.sismember(userZsetKey,userName);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
////
////    /**
////     * @Author:徐子涵
////     * @Date:2020-12-02
////     * @desc:用户进入房间时，增加房间人数计数
////     */
////    public boolean incr(){
////        Jedis jedis = jedisPool.getResource();
////        try{
////            jedis.incrBy(incrKey,incrNum);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return true;
////    }
////
////    /**
////     * @Author:徐子涵
////     * @Date:2020-12-02
////     * @desc:用户离开房间时，减少房间人数计数
////     */
////    public boolean decr(){
////        Jedis jedis = jedisPool.getResource();
////        try{
////            jedis.decrBy(incrKey,incrNum);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return true;
////    }
////
////    public String getNum(){
////        Jedis jedis = jedisPool.getResource();
////        String result = "";
////        try{
////            result = jedis.get(incrKey);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
//
////    public boolean addSet(String userName){
////        Jedis jedis = jedisPool.getResource();
////        try{
////            jedis.sadd(setKey,userName);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return true;
////    }
////
////    public boolean subSet(String userName){
////        Jedis jedis = jedisPool.getResource();
////        try{
////            jedis.srem(setKey,userName);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return true;
////    }
////
////    public Set<String> getSet(){
////        Jedis jedis = jedisPool.getResource();
////        Set<String> result;
////        try{
////            result = jedis.smembers(setKey);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
//
////    public String getDesc(){
////        Jedis jedis = jedisPool.getResource();
////        String result = "";
////        try{
////            result = jedis.get(descKey);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
//
//    public void addZSetUser(String userName){
//        Jedis jedis = jedisPool.getResource();
//        try{
//            jedis.zadd(userZsetKey,new Date().getTime(),userName);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
//    public void subZSetUser(String userName){
//        Jedis jedis = jedisPool.getResource();
//        try{
//            jedis.zrem(userZsetKey,userName);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
////    public Set<String> getZSetUser(){
////        Jedis jedis = jedisPool.getResource();
////        Set<String> result;
////        try{
////            result = jedis.zrevrange(userZsetKey,0,-1);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
//
//    public Set<String> getFREQ(){
//        Jedis jedis = jedisPool.getResource();
//        Set<String> result;
//        try{
//            result = jedis.zrangeByScore(userZsetKey,beginTime,endtime);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//        return result;
//    }
//
//    public void addZSetReward(String userName,double value){
//        Jedis jedis = jedisPool.getResource();
//        try{
//            jedis.zadd(rewardZsetKey,value,userName);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
//    public void incrZSetReward(String userName,double value){
//        Jedis jedis = jedisPool.getResource();
//        try{
//            jedis.zincrby(rewardZsetKey,value,userName);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
////    public void addList(String userName,double value){
////        Jedis jedis = jedisPool.getResource();
////        try{
////            jedis.lpush(listKey,userName +"-"+ new Date().toString() +"-"+value );
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////    }
////
////    public List<String> getList(){
////        Jedis jedis = jedisPool.getResource();
////        List<String> result;
////        try{
////            result = jedis.lrange(listKey,0,-1);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
//
//    public Set<String> getZSetReward(){
//        Jedis jedis = jedisPool.getResource();
//        Set<String> result;
//        try{
//            result = jedis.zrevrange(rewardZsetKey,0,endRange);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//        return result;
//    }
//
////    public List<String> getList(){
////        Jedis jedis = jedisPool.getResource();
////        List<String> result;
////        try{
////            result = jedis.lrange(listKey,0,-1);
////        }
////        catch (Exception e){
////            throw e;
////        }
////        finally {
////            jedisPool.returnResource(jedis);
////        }
////        return result;
////    }
//
//    public void clean(){
//        Jedis jedis = jedisPool.getResource();
//        try{
//            jedis.del(incrKey);
//            jedis.del(setKey);
//            jedis.del(userZsetKey);
//            jedis.del(rewardZsetKey);
//            jedis.del(listKey);
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
//
//
//}
