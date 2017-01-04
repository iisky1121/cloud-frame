package com.jfinal.ext.plugin.validate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Repeatable(Validations.class)
public @interface Validation {
	String name();//参数名称
	Class<?> type();//参数类型
    boolean required() default false;//是否必填
    String regExp() default "";//正则表达式
	String groovyExp() default "";//groovy表达式
}

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@interface Validations{
	Validation[] value();
}
