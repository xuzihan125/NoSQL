package com.redis.project.counter;

import com.redis.project.util.ClassUtil;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;

public class BaseCounter {
    protected CounterConfig counterConfig;
    protected Jedis jedis;

    public BaseCounter(CounterConfig counterConfig, Jedis jedis) {
        this.counterConfig = counterConfig;
        this.jedis = jedis;
    }



    public String redisOp(){
        if(!ClassUtil.checkEmpty(counterConfig)){
            throw new RuntimeException("操作参数不可为空");
        }
        if(counterConfig.getExpireTime()<=0){
            throw new RuntimeException("过期时间设置错误");
        }
        return "";
    }
}
