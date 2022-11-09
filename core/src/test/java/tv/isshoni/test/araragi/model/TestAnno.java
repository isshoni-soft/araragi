package tv.isshoni.test.araragi.model;

import tv.isshoni.araragi.reflect.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TestAnno {

    @AliasFor(name = "other")
    String value() default "";

    String other() default "";
}
