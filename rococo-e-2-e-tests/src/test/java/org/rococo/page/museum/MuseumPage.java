package org.rococo.page.museum;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.model.MuseumJson;
import org.rococo.page.BasePage;
import org.rococo.page.component.Image;
import org.rococo.page.component.museum.EditingMuseumForm;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MuseumPage extends BasePage<MuseumPage> {

    public static final String URL = CFG.frontUrl() + "museum/";

    private final SelenideElement img = $("article img");
    private final SelenideElement name = $("article header");
    private final SelenideElement location = $("article div.text-center");
    private final SelenideElement editBtn = $("article button");
    private final SelenideElement description = $("#description");

    private final Image image = new Image(img);

    @Override
    public MuseumPage checkThatPageLoaded() {
        img.should(visible);
        name.should(visible);
        editBtn.should(visible);
        return this;
    }

    @Step("Start editing a museum")
    @Nonnull
    public EditingMuseumForm editMuseum() {
        editBtn.click();
        return new EditingMuseumForm();
    }


    @Step("Check museum info: {expected}")
    @Nonnull
    public MuseumPage checkMuseumInfo(MuseumJson expected) {
        name.should(visible).shouldHave(text(expected.title()));
        description.should(visible).shouldHave(text(expected.description()));
        location.should(visible).shouldHave(
                text(
                        expected.geo().country().name() + ", " + expected.geo().city()
                )
        );

        return this;
    }

    @Step("Check photo on Museum page")
    @Nonnull
    public MuseumPage checkPhoto(BufferedImage expected) throws IOException {
        image.checkPhoto(expected);
        return this;
    }

    @Step("Check museum picture exist")
    @Nonnull
    public MuseumPage checkPhotoExist() {
        image.checkPhotoExist();
        return this;
    }

    public static String url(String id) {
        return URL + id;
    }
}
