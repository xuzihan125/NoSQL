package com.redis.project.controller;


import com.redis.project.entity.OperationDTO;
import com.redis.project.entity.OperationVO;
import com.redis.project.service.SystemService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


//@RestController(value = "/redis")
@ComponentScan
@RestController
@RequestMapping(value = "/redis")
public class RedisController {
    @Resource
    private SystemService systemService;

    @RequestMapping(value="/operation",method= RequestMethod.POST)
    public OperationVO processOperation(@RequestBody OperationDTO operationDTO){
        return systemService.processOperation(operationDTO);
    }
}

/**
 *         System.out.println("\nRedis直播间统计系统：");
 *         System.out.println("0.进入房间");
 *         System.out.println("1.退出房间");
 *         System.out.println("2.打赏");
 *         // str
 *         System.out.println("3.房间描述");
 *         // num
 *         System.out.println("4.总人数统计");
 *         // set
 *         System.out.println("5.历史浏览人员");
 *         // list
 *         System.out.println("6.给定时间内浏览人员");
 *         // list
 *         System.out.println("7.打赏历史");
 *         // zset
 *         System.out.println("8.打赏排行榜");
 *         System.out.println("9.清空历史记录");
 *         System.out.println("10.退出");
 */
