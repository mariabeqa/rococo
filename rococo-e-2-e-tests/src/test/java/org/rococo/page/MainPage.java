package org.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.page.artist.ArtistsPage;
import org.rococo.page.component.Header;
import org.rococo.page.component.Profile;
import org.rococo.page.museum.MuseumsPage;
import org.rococo.page.painting.PaintingsPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl();

    protected final Header header = new Header();
    protected final SelenideElement paintings = $("img[alt='Ссылка на картины']");
    protected final SelenideElement artists = $("img[alt='Ссылка на художников']");
    protected final SelenideElement museums = $("img[alt='Ссылка на музеи']");
    protected final SelenideElement mainText = $("p.text-center");

    @Override
    @Step("Check that Main page is loaded")
    @Nonnull
    public MainPage checkThatPageLoaded() {
        header.getSelf().should(visible).shouldHave(text("Rococo"));
        paintings.should(visible);
        artists.should(visible);
        museums.should(visible);
        return this;
    }

    @Step("Open Paintings page")
    @Nonnull
    public PaintingsPage toPaintingsPage() {
        paintings.click();
        return new PaintingsPage();
    }

    @Step("Open Artists page")
    @Nonnull
    public ArtistsPage toArtistsPage() {
        artists.click();
        return new ArtistsPage();
    }

    @Step("Open Museums page")
    @Nonnull
    public MuseumsPage toMuseumsPage() {
        museums.click();
        return new MuseumsPage();
    }

    @Step("To Login page")
    @Nonnull
    public LoginPage toLoginPage() {
        header.logIn();
        return new LoginPage();
    }

    @Nonnull
    public Header header() {
        return header;
    }
}
