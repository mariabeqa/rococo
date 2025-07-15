package org.rococo.utils.data;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import java.util.Locale;

public class RandomDataUtils {

    private static final Faker FAKER = new Faker(Locale.of("ru"));

    @Nonnull
    public static String randomUsername() {
        return FAKER.name().username();
    }

    @Nonnull
    public static String randomPassword() {
        return FAKER.internet().password(5, 10);
    }

    @Nonnull
    public static String randomName() {
        return FAKER.name().firstName();
    }

    @Nonnull
    public static String randomSurname() {
        return FAKER.name().lastName();
    }

    @Nonnull
    public static String randomSentence(int wordsCount) {
        return FAKER.lorem().sentence(wordsCount);
    }

    public static String randomMuseumTitle() {
        return "Государственный русский музей №" + randomNumber(1, 9999);
    }

    public static String randomMuseumDescription() {
        return randomSentence(20);
    }

    public static String randomArtistBio() {
        return randomSentence(20);
    }

    public static String randomArtistName() {
        return FAKER.name().fullName();
    }

    public static String randomPaintingName() {
        return FAKER.name().title();
    }

    public static String randomPaintingDescription() {
        return randomSentence(20);
    }

    public static int randomNumber(int min, int max) {
        return FAKER.number().numberBetween(min, max);
    }
}
