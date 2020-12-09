package com.redis.project.counter;

import com.redis.project.util.ClassUtil;
import com.redis.project.util.EnumCamp;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ZSetCounter extends BaseCounter {

    public ZSetCounter(CounterConfig counterConfig, Jedis jedis) {
        super(counterConfig, jedis);
    }

    @Override
    public String redisOp(){
        super.redisOp();
        String opType = counterConfig.getCounterName();
        if(!jedis.type(counterConfig.getKey()).equals("none") && !jedis.type(counterConfig.getKey()).equals("zset")){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }
        String result = "";
        if(opType.equals(EnumCamp.CounterType.ADDZSET.getCode())){
            if(counterConfig.getValue().split("\\s+").length<2){
                throw new RuntimeException("输入参数不足\n");
            }
            if(jedis.zrank(counterConfig.getKey(),counterConfig.getValue().split("\\s+")[0])!=null){
                throw new RuntimeException("value已存在\n");
            }
            try{
                jedis.zadd(counterConfig.getKey(), Long.parseLong(counterConfig.getValue().split("\\s+")[1]),counterConfig.getValue().split("\\s+")[0]);
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "zset插入成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.SUBZSET.getCode())){
            if(jedis.zrank(counterConfig.getKey(),counterConfig.getValue().split("\\s+")[0]) == null){
                throw new RuntimeException("value不存在\n");
            }
            try{
                jedis.zrem(counterConfig.getKey(),counterConfig.getValue());
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "zset删除成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.ADDZSETVALUE.getCode())){
            if(counterConfig.getValue().split("\\s+").length<2){
                throw new RuntimeException("输入参数不足\n");
            }
            try{
                jedis.zincrby(counterConfig.getKey(),Double.parseDouble(counterConfig.getValue().split("\\s+")[1]),counterConfig.getValue().split("\\s+")[0]);
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "zset插入成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.GETZSETBYVALUE.getCode())){
            if(counterConfig.getValue().split("\\s+").length<2){
                throw new RuntimeException("输入参数不足\n");
            }
            try{
                long startIndex = Long.parseLong(counterConfig.getValue().split("\\s+")[0]);
                long endIndex = Long.parseLong(counterConfig.getValue().split("\\s+")[1]);
                Set<String> tempo = jedis.zrevrange(counterConfig.getKey(),startIndex,endIndex);
                if(tempo.size()==0){
                    return "无满足要求数据";
                }
                Iterator<String> it = tempo.iterator();
                int i=1;
                while(it.hasNext()){
                    result += i+"."+it.next()+"\n";
                    i++;
                }
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
        }
        else if(opType.equals(EnumCamp.CounterType.GETZSETBYSCORE.getCode())){
            if(counterConfig.getValue().split("\\s+").length<2){
                throw new RuntimeException("输入参数不足\n");
            }
            try{
                long minScore =  Long.parseLong(counterConfig.getValue().split("\\s+")[0]);
                long maxScore =  Long.parseLong(counterConfig.getValue().split("\\s+")[1]);
                Set<String> tempo = jedis.zrangeByScore(counterConfig.getKey(),minScore,maxScore);
                if(tempo.size()==0){
                    return "无满足要求数据";
                }
                Iterator<String> it = tempo.iterator();
                int i=1;
                while(it.hasNext()){
                    result += i+"."+it.next()+"\n";
                    i++;
                }
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
        }
        else if(opType.equals(EnumCamp.CounterType.FREQ.getCode())){
            if(counterConfig.getValue().split("\\s+").length<2){
                throw new RuntimeException("输入参数不足\n");
            }
            try{
                long minScore = ClassUtil.dateStringToLong(counterConfig.getValue().split("\\s+")[0]);
                long maxScore = ClassUtil.dateStringToLong(counterConfig.getValue().split("\\s+")[1]);
                Set<String> tempo = jedis.zrangeByScore(counterConfig.getKey(),minScore,maxScore);
                Iterator<String> it = tempo.iterator();
                long count =0;
                while(it.hasNext()){
                    String record = it.next();
                    String type =record.split(".")[1].split("-")[0];
                    if(type.equals("in")){
                        count ++;
                    }
                    else if(type.equals("out")){
                        count --;
                    }
                }
                result = "在给定时间内，房间人数变化情况为："+count+"\n";
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
        }
        else{
            throw new RuntimeException("操作类别错误\n");
        }
        return result;
    }
}
