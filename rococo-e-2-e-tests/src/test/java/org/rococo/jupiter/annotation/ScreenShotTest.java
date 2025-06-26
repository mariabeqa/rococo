package org.rococo.jupiter.annotation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rococo.jupiter.extension.ScreenShotTestExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Test
@ExtendWith(ScreenShotTestExtension.class)
public @interface ScreenShotTest {
  String expected();
  boolean rewriteExpected() default false;
}
