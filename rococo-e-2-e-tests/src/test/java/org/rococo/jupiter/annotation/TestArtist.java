package org.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestArtist {
    String name () default "";
    String bio() default "";
    String photoPath() default "";
    boolean removeAfterTest() default true;
}
