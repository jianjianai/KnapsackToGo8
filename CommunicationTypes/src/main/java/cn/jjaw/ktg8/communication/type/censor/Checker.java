package cn.jjaw.ktg8.communication.type.censor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 检查器
 */
public interface Checker{
    Class<? extends Annotation> getAnnotationClass();
    default boolean checkField(Field field, Object obj){
        return true;
    }
    default boolean checkObject(Object obj){
        return true;
    }
}
