package com.redis.project.counter;

import com.redis.project.util.ClassUtil;
import com.redis.project.util.EnumCamp;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Set;

public class SetCounter extends BaseCounter{
    public SetCounter(CounterConfig counterConfig, Jedis jedis) {
        super(counterConfig, jedis);
    }

    @Override
    public String redisOp(){
        super.redisOp();
        String opType = counterConfig.getCounterName();
        if(!jedis.type(counterConfig.getKey()).equals("none") && !jedis.type(counterConfig.getKey()).equals("set")){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }
        String result = "";
        if(opType.equals(EnumCamp.CounterType.ADDSET.getCode())){
            try{
                jedis.sadd(counterConfig.getKey(),counterConfig.getValue());
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "set插入成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.SUBSET.getCode())){
            try{
                jedis.srem(counterConfig.getKey(),counterConfig.getValue());
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "set删除成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.CHECKSET.getCode())){
            if(counterConfig.getValue().split("\\s+").length<2){
                throw new RuntimeException("输入参数不足\n");
            }
            boolean type=Boolean.parseBoolean(counterConfig.getValue().split("\\s+")[1]);
            if(jedis.sismember(counterConfig.getKey(),counterConfig.getValue().split("\\s+")[0])!=type){
                if(type){
                    throw new RuntimeException("操作对象不存在\n");
                }
                else{
                    throw new RuntimeException("操作对象已存在\n");
                }
            }
            result = "";
        }
        else{
            throw new RuntimeException("操作类别错误\n");
        }
        return result;
    }
}
