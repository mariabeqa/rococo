package org.rococo.page.painting;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.model.PaintingJson;
import org.rococo.page.BasePage;
import org.rococo.page.component.Image;
import org.rococo.page.component.Select;
import org.rococo.page.component.painting.EditingPaintingForm;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class PaintingPage extends BasePage<PaintingPage> {

    public static final String URL = CFG.frontUrl() + "painting/";

    private final SelenideElement img = $("article img");
    private final SelenideElement name = $("article header");
    private final SelenideElement museum = $("#museum");
    private final SelenideElement artist = $("#author");
    private final SelenideElement editBtn = $("article button");
    private final SelenideElement description = $("#description");

    private final Image image = new Image(img);

    @Override
    public PaintingPage checkThatPageLoaded() {
        name.should(visible);
        editBtn.should(visible);
        return this;
    }

    @Step("Start editing a painting")
    @Nonnull
    public EditingPaintingForm editPainting() {
        Selenide.sleep(2000);
        editBtn.click();
        return new EditingPaintingForm();
    }

    @Step("Check picture on Painting page")
    @Nonnull
    public PaintingPage checkPhoto(BufferedImage expected) throws IOException {
        image.checkPhoto(expected);
        return this;
    }

    @Step("Check painting image exist")
    @Nonnull
    public PaintingPage checkPhotoExist() {
        image.checkPhotoExist();
        return this;
    }

    @Step("Check painting info: {expected}")
    public void checkPaintingInfo(PaintingJson expected) {
        name.should(visible).shouldHave(text(expected.title()));
        description.should(visible).shouldHave(text(expected.description()));
        assertEquals(
                expected.museum().title(),
                museum.getText()
        );
    }

    public static String url(String id) {
        return URL + id;
    }
}
