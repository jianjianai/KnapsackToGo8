package cn.jjaw.ktg8.communication.type.censor.checker;

import cn.jjaw.ktg8.communication.type.censor.Checker;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static cn.jjaw.ktg8.communication.type.Logger.logger;

/**
 * 字段非空检测器
 */
public class NotNuller implements Checker {
    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return NotNull.class;
    }

    @Override
    public boolean checkField(Field field, Object obj) {
        try {
            if(field.get(obj)==null){
                logger.error(field+"为 null");
                return false;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }
}
