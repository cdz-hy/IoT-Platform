package com.iot.platform.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解 - 标记需要记录详细操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /** 操作模块 */
    String module() default "";

    /** 操作描述 */
    String description() default "";

    /** 操作类型：CREATE, READ, UPDATE, DELETE, COMMAND, LOGIN, OTHER */
    String type() default "OTHER";
}
