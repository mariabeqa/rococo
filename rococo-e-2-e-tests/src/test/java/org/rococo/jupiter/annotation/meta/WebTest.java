package org.rococo.jupiter.annotation.meta;

import org.rococo.jupiter.extension.*;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rococo.jupiter.extension.TestUserExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        SelenideConfigurationExtension.class,
        TestUserExtension.class,
        MuseumExtension.class,
        ArtistExtension.class,
        PaintingExtension.class,
        ApiLoginExtension.class,
        AllureJunit5.class
})
public @interface WebTest {
}
