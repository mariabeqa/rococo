package org.rococo.jupiter.annotation;

import org.rococo.model.Countries;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestMuseum {
    String title() default "";
    String description() default "";
    Countries country() default Countries.RUSSIA;
    String city() default "";
    String path() default "";
    boolean removeAfterTest() default true;
}
