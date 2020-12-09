package com.redis.project.counter;

import com.redis.project.util.EnumCamp;
import redis.clients.jedis.Jedis;

import java.util.List;

public class OpCounter extends BaseCounter{
    public OpCounter(CounterConfig counterConfig, Jedis jedis) {
        super(counterConfig, jedis);
    }

    @Override
    public String redisOp(){
        super.redisOp();
        String opType = counterConfig.getCounterName();
        String result = "";
        if(opType.equals(EnumCamp.CounterType.DEL.getCode())){
            try{
                String[] tempo = counterConfig.getValue().split("\\s+");
                for (String s : tempo) {
                    jedis.del(s);
                }
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "数据删除成功\n";
        }
        else{
            throw new RuntimeException("操作类别错误\n");
        }
        return result;
    }

}
