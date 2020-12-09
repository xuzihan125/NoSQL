package com.redis.project.service;


import com.redis.project.entity.OperationDTO;
import com.redis.project.entity.OperationVO;

public interface SystemService {
    OperationVO processOperation(OperationDTO operationDTO);

}
