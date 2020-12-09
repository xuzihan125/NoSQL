package com.redis.project.util;

import java.lang.reflect.Field;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

public class ClassUtil {
    // true 不为空  false 为空
    public static boolean checkEmpty(Object object) {
        boolean flag = false;
        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                if (f.get(object) == null || f.get(object) == "") {
                } else {
                    flag = true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return flag;
    }

    public static long dateStringToLong(String date){
        DateFormat dateFormat = EnumCamp.TimeFormate.TIME_FORMATE.getDateFormat();
        long result = -1;
        try{
            result = dateFormat.parse(date).getTime();
        }catch (Exception e){
            throw new RuntimeException("日期格式错误");
        }
        return result;



    }
}
