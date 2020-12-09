package com.redis.project.counter;

import com.redis.project.util.EnumCamp;
import redis.clients.jedis.Jedis;

import java.util.List;

public class ListCounter extends BaseCounter{
    public ListCounter(CounterConfig counterConfig, Jedis jedis) {
        super(counterConfig, jedis);
    }

    @Override
    public String redisOp(){
        super.redisOp();
        String opType = counterConfig.getCounterName();
        if(!jedis.type(counterConfig.getKey()).equals("none") && !jedis.type(counterConfig.getKey()).equals("list")){
            throw new RuntimeException("key对应类型与操作类型不符\n");
        }
        String result = "";
        if(opType.equals(EnumCamp.CounterType.ADDLIST.getCode())){
            try{
                jedis.lpush(counterConfig.getKey(), counterConfig.getValue());
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "数据插入成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.SUBLIST.getCode())){
            try{
                if(counterConfig.getValue().split("\\s+").length<2){
                    throw new RuntimeException("输入参数不足\n");
                }
                long count = Long.parseLong(counterConfig.getValue().split("\\s+")[0]);
                String value = counterConfig.getValue().split("\\s+")[1];
                jedis.lrem(counterConfig.getKey(), count,value);
                jedis.expire(counterConfig.getKey(),counterConfig.getExpireTime());
            }catch(Exception e){
                throw new RuntimeException("redis操作失败\n");
            }
            result = "数据删除成功\n";
        }
        else if(opType.equals(EnumCamp.CounterType.GETLIST.getCode())){
            try{
                if(counterConfig.getValue().split("\\s+").length<2){
                    throw new RuntimeException("输入参数不足\n");
                }
                long startIndex = Long.parseLong(counterConfig.getValue().split("\\s+")[0]);
                long endIndex = Long.parseLong(counterConfig.getValue().split("\\s+")[1]);
                List<String> tempo = jedis.lrange(counterConfig.getKey(), startIndex,endIndex);
                if(tempo.size() == 0){
                    result = "无符合要求的数据";
                }
                else{
                    for(int i=0;i<tempo.size();i++) {
                        result += (i+1)+"."+ tempo.get(i)+"\n";
                    }
                }
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
