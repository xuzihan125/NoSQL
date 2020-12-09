package com.redis.project.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EnumCamp {
    public enum Directory{

        COUNTER_CONFIG_PATH("src\\main\\java\\com\\redis\\project\\config\\","counter.json"),
        ACTION_CONFIG_PATH("src\\main\\java\\com\\redis\\project\\config\\","action.json")
        ;

        private String dir;
        private String file;

        private Directory(String dir, String file){
            this.dir = dir;
            this.file = file;
        }

        public String getPath(){return dir+file;}
        public String getDir(){return dir;}
        public String getFile(){return file;}
    }

    public enum CounterType{
        INCR("incr"),
        DECR("decr"),
        GETNUM("getNum"),
        GETDESC("getDesc"),
        SETDESC("setDesc"),
        ADDLIST("addList"),
        SUBLIST("subList"),
        GETLIST("getList"),
        SUBSET("subSet"),
        ADDSET("addSet"),
        CHECKSET("checkSet"),
        SUBZSET("subZSet"),
        ADDZSET("addZSet"),
        SUBZSETVALUE("subZSetValue"),
        ADDZSETVALUE("addZSetValue"),
        GETZSETBYVALUE("getZSetByValue"),
        GETZSETBYSCORE("getZSetByScore"),
        DEL("del"),
        FREQ("freq")
        ;

        private String code;
        private CounterType(String code){
            this.code = code;
        }
        public String getCode(){return code;}
    }

    public enum TimeFormate{
        TIME_FORMATE("yyyy-MM-dd-HH:mm:ss");

        private DateFormat dateFormat;
        private TimeFormate(String code){
            this.dateFormat = new SimpleDateFormat(code);
        }
        public DateFormat getDateFormat(){return dateFormat;}
    }

    public enum Value{

        TYPE("actionName"),
        NAME("name"),
        VALUE("value"),
        STATE("state"),
        FEEDBACK("feedBack"),
        STRINGLIST("stringList"),
        ;


        private String code;
        private Value(String code){
            this.code = code;
        }
        public String getCode(){return code;}
    }

    public enum Connection{
        LOCAL("http://127.0.0.1:8008/redis/operation");
        private String url;
        private Connection(String url){
            this.url = url;
        }
        public String getUrl(){return url;}
    }
}
