package com.jfinal.ext.plugin.uribind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UriBind {
    String rule();//规则 key
    int level() default 0;//等级，执行顺序1-2-3，默认0
    boolean before() default true;//是否在拦截uri之前执行，默认true
    boolean async() default true;//是否异步执行，默认true
}
