package cn.jjaw.ktg8.communication.type.censor.checker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 确保某字段不为null
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
}