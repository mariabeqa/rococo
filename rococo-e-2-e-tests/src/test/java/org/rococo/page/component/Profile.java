package org.rococo.page.component;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.rococo.jupiter.extension.ScreenShotTestExtension;
import org.rococo.page.MainPage;
import org.rococo.utils.ScreenDiffResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class Profile extends BaseComponent<Profile> {

    protected Profile() {
        super($("form.modal-form"));
    }

    private final SelenideElement logoutBtn  = self.$("button.variant-ghost");
    private final SelenideElement photoInput = self.$("input[type='file']");
    private final SelenideElement firstname = self.$("input[name='firstname']");
    private final SelenideElement lastname = self.$("input[name='surname']");
    private final SelenideElement closeBtn = self.$(By.xpath("//button[text()='Закрыть']"));
    private final SelenideElement saveBtn = self.$("button[type='submit']");
    private final SelenideElement avatar = self.$("img.avatar-image");

    @Step("Log out")
    @Nonnull
    public MainPage signOut() {
        logoutBtn.click();
        return new MainPage();
    }

    @Step("Set firstname: '{0}'")
    @Nonnull
    public Profile setFirstName(String name) {
        firstname.clear();
        firstname.setValue(name);
        return this;
    }

    @Step("Set lastname: '{0}'")
    @Nonnull
    public Profile setLastName(String name) {
        lastname.clear();
        lastname.setValue(name);
        return this;
    }

    @Step("Check firstname is set to: '{0}'")
    @Nonnull
    public Profile checkFirstName(String name) {
        firstname.shouldHave(value(name));
        return this;
    }

    @Step("Check lastname is set to: '{0}'")
    @Nonnull
    public Profile checkLastName(String name) {
        lastname.shouldHave(value(name));
        return this;
    }

    @Step("Upload photo from classpath: '{0}'")
    @Nonnull
    public Profile uploadPhoto(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Check photo in Profile")
    @Nonnull
    public Profile checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
        assertFalse(
                new ScreenDiffResult(
                        actualImage, expected
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    @Step("Check profile picture exist in profile")
    @Nonnull
    public Profile checkPhotoExist() {
        avatar.should(attributeMatching("src", "data:image.*"));
        return this;
    }

    @Step("Submit form")
    @Nonnull
    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
