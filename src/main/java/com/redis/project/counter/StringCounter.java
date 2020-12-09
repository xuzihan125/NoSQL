package com.redis.project.counter;

import com.redis.project.util.EnumCamp;
import redis.clients.jedis.Jedis;

public class StringCounter extends BaseCounter{
    public StringCounter(CounterConfig counterConfig, Jedis jedis) {
        super(counterConfig, jedis);
    }

    @Override
    public String redisOp() {
        super.redisOp();
        String opType = counterConfig.getCounterName();
        if(jedis.type(counterConfig.getKey()).equals("none")){
            jedis.set(counterConfig.getKey(),"");
        }
        else if(!jedis.type(counterConfig.getKey()).equals("string")){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }
        String result = "";
        if(opType.equals(EnumCamp.CounterType.SETDESC.getCode())){
            try{
                jedis.set(counterConfig.getKey(),counterConfig.getValue());
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "描述修改成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.GETDESC.getCode())){
            String value;
            try{
                value = jedis.get(counterConfig.getKey());
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
                if(value == null){
                    value = "";
                }
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "描述为:\n"+value+"\n";
        }
        else{
            throw new RuntimeException("操作类别错误\n");
        }
        return result;
    }
}
