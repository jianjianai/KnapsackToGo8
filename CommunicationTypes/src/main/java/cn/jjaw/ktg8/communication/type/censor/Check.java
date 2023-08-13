package cn.jjaw.ktg8.communication.type.censor;


import cn.jjaw.ktg8.communication.type.censor.checker.AllNotNuller;
import cn.jjaw.ktg8.communication.type.censor.checker.NotNuller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Check {
    static List<Checker> checkerList = new ArrayList<>(){{
        add(new NotNuller());
        add(new AllNotNuller());
    }};

    /**
     * 检查对象是否合法，如果不合法则输出错误信息
     * 只能检查public字段
     * @return null 则不合法或者原对象为空，否则返回源对象
     */
    public static <T> T check(T obj){
        return check(obj,true);
    }
    /**
     * 检查对象是否合法，如果不合法则输出错误信息
     * 只能检查public字段
     * @return null 则不合法或者原对象为空，否则返回源对象
     * @param nullError 是否打印输出空指针异常
     */
    public static <T> T check(T obj,boolean nullError){
        if(obj==null){
            if(nullError){
                new NullPointerException().printStackTrace();
            }
            return null;
        }
        //检查对象
        for (Checker checker : checkerList) {
            if (obj.getClass().getAnnotation(checker.getAnnotationClass())==null) {
                continue;
            }
            if (!checker.checkObject(obj)) {
                return null;
            }
        }
        //检查字段
        for (Field field : obj.getClass().getFields()) {
            for (Checker checker : checkerList) {
                if (field.getAnnotation(checker.getAnnotationClass()) == null) {
                    continue;
                }
                if (!checker.checkField(field, obj)) {
                    return null;
                }
            }
        }
        return obj;
    }
}


