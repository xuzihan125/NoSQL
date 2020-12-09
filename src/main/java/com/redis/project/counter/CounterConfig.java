package com.redis.project.counter;

import lombok.Data;

@Data
public class CounterConfig implements Cloneable{
    private String name;
    private String counterName;
    private String desc;
    private String key;
    private String type;
    private String value;
    private int expireTime;

    @Override
    public CounterConfig clone() throws CloneNotSupportedException{
        return (CounterConfig) super.clone();
    }
}
