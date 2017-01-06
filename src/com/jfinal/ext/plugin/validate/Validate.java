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
@Repeatable(Validates.class)
public @interface Validate {
	String name();//参数名称
    boolean required() default false;//是否必填
    String description() default "";//描述
	String groovyExp() default "";//groovy表达式
	Class<?> enumClass() default Enum.class;//枚举检查
	String[] eitherOr() default {};//多选一
    String[] association() default {};//关联必填
}

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@interface Validates{
	Validate[] value();
}
