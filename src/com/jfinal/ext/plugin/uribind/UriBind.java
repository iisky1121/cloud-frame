package com.jfinal.ext.plugin.uribind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UriBind {
    String uri();//拦截的url
    boolean beforeAsync() default false;//是否异步执行，默认false,该场景一般是使用在拦截，并对参数进行注入，必须同步才能处理
    boolean afterAsync() default true;//是否异步执行，默认true，该场景一般是使用在统计，只需要统计还执行的结果，异步处理更优
}
