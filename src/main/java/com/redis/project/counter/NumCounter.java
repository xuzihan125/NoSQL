package com.redis.project.counter;

import com.redis.project.util.ClassUtil;
import com.redis.project.util.EnumCamp;
import redis.clients.jedis.Jedis;

public class NumCounter extends BaseCounter{
    public NumCounter(CounterConfig counterConfig, Jedis jedis) {
        super(counterConfig, jedis);
    }

    @Override
    public String redisOp() {
        super.redisOp();
        String tempo = jedis.type(counterConfig.getKey());
        if(jedis.type(counterConfig.getKey()).equals("none")){
            jedis.set(counterConfig.getKey(),"0");
        }
        else if(!jedis.type(counterConfig.getKey()).equals("string")){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }
        try{
            Long.parseLong(jedis.get(counterConfig.getKey()));
        }catch (Exception e){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }
        String opType = counterConfig.getCounterName();
        try{
            String check = jedis.get(counterConfig.getKey());
            if(jedis.get(counterConfig.getKey())==null){
                jedis.set(counterConfig.getKey(),"0");
            }
            Long.valueOf(check);
        }catch(Exception e){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }

        String result = "";
        if(opType.equals(EnumCamp.CounterType.INCR.getCode())){
            try{
                jedis.incrBy(counterConfig.getKey(), Long.parseLong(counterConfig.getValue()));
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "num增加成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.DECR.getCode())){
            try{
                jedis.decrBy(counterConfig.getKey(), Long.parseLong(counterConfig.getValue()));
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "num减少成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.GETNUM.getCode())){
            String value;
            try{
                value = jedis.get(counterConfig.getKey());
                if(value == null){
                    value = "0";
                }
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "num为"+value+"\n";
        }
        else{
            throw new RuntimeException("操作类别错误\n");
        }
        return result;
    }
}
