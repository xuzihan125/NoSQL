package com.redis.project.action;

import lombok.Data;

import java.util.List;

@Data
public class ActionConfig {
    private String name;
    private String desc;
    private List<String> counter;
}
