package cn.dezhe.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用来设置表名
 */
@Retention(RetentionPolicy.RUNTIME)  //运行期间保留注释信息
@Target(ElementType.TYPE)   //设置注解在什么地方（类）
public @interface ORMTable {

    public String name() default "";

}
