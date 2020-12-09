package com.redis.project.service.SerivceImpl;


import com.redis.project.action.ActionConfig;
import com.redis.project.action.ActionConfigLoader;
import com.redis.project.counter.BaseCounter;
import com.redis.project.counter.CounterConfig;
import com.redis.project.counter.CounterConfigLoader;
import com.redis.project.entity.OperationDTO;
import com.redis.project.entity.OperationVO;
import com.redis.project.service.SystemService;
import com.redis.project.util.ClassUtil;
import com.redis.project.util.JedisInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SystemServiceImpl implements SystemService, CommandLineRunner {
//    @Resource
//    private RedisOperation redisOperation;
    @Autowired
    private ActionConfigLoader actionConfigLoader;
    @Autowired
    private CounterConfigLoader counterConfigLoader;

    @Override
    public void run(String... args) throws Exception {
        OperationDTO operationDTO = new OperationDTO();
        operationDTO.setActionName("init");
        processOperation(operationDTO);
    }

    @Override
    public OperationVO processOperation(OperationDTO operationDTO){
        String message = "";
        JedisPool jedisPool = JedisInstance.getInstance();
        Jedis jedis = jedisPool.getResource();
        try{
            ActionConfig actionConfig = actionConfigLoader.getAction(operationDTO.getActionName());
            List<String> counterList = actionConfig.getCounter();
            for (String s : counterList) {
                CounterConfig counterConfig = counterConfigLoader.getCounterConfig(s).clone();
                counterConfig.setValue(transform(counterConfig.getValue(),operationDTO));
                BaseCounter counter = (BaseCounter) counterConfigLoader.getCounter(counterConfig.getType()).getConstructor(CounterConfig.class,Jedis.class).newInstance(counterConfig, jedis);
                message = message + counter.redisOp();
            }
        }
        catch(Exception e){
            OperationVO entityVO = new OperationVO();
            entityVO.setState("fail");
            entityVO.setFeedBack(e.getMessage());
            return entityVO;
        }
        finally {
            jedisPool.returnResource(jedis);
        }
        OperationVO entityVO = new OperationVO();
        entityVO.setState("success");
        entityVO.setFeedBack(message);
        return entityVO;
    }

    public String transform(String value, OperationDTO operationDTO){
        value = value.replaceAll("\\{user\\}", operationDTO.getName());
        value = value.replaceAll("\\{value\\}", operationDTO.getValue());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        value = value.replaceAll("\\{currentTimeString\\}", formatter.format(new Date()));
        value = value.replaceAll("\\{currentTime\\}", String.valueOf(ClassUtil.dateStringToLong(formatter.format(new Date()))));
        return value;
    }

//    public OperationVO enter(String name){
//        if(!redisOperation.check(name)){
//            throw new RuntimeException(name+"用户已在房间内");
//        }
//        redisOperation.incr();
//        redisOperation.addZSetUser(name);
//        redisOperation.addZSetReward(name,0);
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack(name+"用户进入房间");
//        return operationVO;
//    }
//
//    public OperationVO leave(String name){
//        if(!redisOperation.check(name)){
//            throw new RuntimeException(name+"用户不在房间内");
//        }
//        redisOperation.decr();
//        redisOperation.subZSetUser(name);
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack(name+"用户退出房间");
//        return operationVO;
//    }
//
//    public OperationVO reward(String name, double value){
//        if(!redisOperation.check(name)){
//            throw new RuntimeException(name+"用户不在房间内");
//        }
//        if(value<=0){
//            throw new RuntimeException("打赏金额"+name+"不可用");
//        }
//        redisOperation.incrZSetReward(name,value);
//        redisOperation.addList(name,value);
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack(name+"用户打赏"+value+"元成功");
//        return operationVO;
//    }
//
//    public OperationVO showNum(){
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("当前房间人数为："+redisOperation.getNum());
//        return operationVO;
//    }
//
//    public OperationVO getDesc(){
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("当前房间描述为：\n"+redisOperation.getDesc());
//        return operationVO;
//    }
//
//    public OperationVO getAllEntered(){
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("进入过该房间的人员名单如下：\n");
//        operationVO.setStringList(new ArrayList<>(redisOperation.getZSetUser()));
//        return operationVO;
//    }
//
//    public OperationVO getFREQ(){
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("一段时间内进入过该房间的人员名单如下：\n");
//        operationVO.setStringList(new ArrayList<>(redisOperation.getFREQ()));
//        return operationVO;
//    }
//
//    public OperationVO getRewardHistory(){
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("该房间打赏历史如下：\n");
//        operationVO.setStringList(redisOperation.getList());
//        return operationVO;
//    }
//
//    public OperationVO getRewardRange(){
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("该房间打赏排行榜如下：\n");
//        operationVO.setStringList(new ArrayList<>(redisOperation.getZSetReward()));
//        return operationVO;
//    }
//
//    public OperationVO clean(){
//        redisOperation.clean();
//        OperationVO operationVO = new OperationVO();
//        operationVO.setState(true);
//        operationVO.setFeedBack("清除完毕。");
//        return operationVO;
//    }
}
