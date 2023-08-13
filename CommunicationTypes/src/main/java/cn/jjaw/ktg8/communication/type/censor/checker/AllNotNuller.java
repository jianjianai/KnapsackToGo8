package cn.jjaw.ktg8.communication.type.censor.checker;

import cn.jjaw.ktg8.communication.type.censor.Checker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import static cn.jjaw.ktg8.communication.type.Logger.logger;

/**
 * 确保对象的所有字段不为null
 */
public class AllNotNuller implements Checker {
    @Override
    public Class<? extends Annotation> getAnnotationClass() {
        return AllNotNull.class;
    }

    @Override
    public boolean checkObject(Object obj) {
        for (Field field : obj.getClass().getFields()) {
            try {
                if(field.get(obj)==null){
                    logger.error(field+"为 null");
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
