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
    String uri();//规则 key
    boolean async() default true;//是否异步执行，默认true
}
