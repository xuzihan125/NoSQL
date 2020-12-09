package com.redis.project.util;

import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;

public class MyFileFilter implements IOFileFilter {
    @Override
    public boolean accept(File file){
        return true;
    }

    @Override
    public boolean accept(File dir,String name){
        return true;
    }
}
