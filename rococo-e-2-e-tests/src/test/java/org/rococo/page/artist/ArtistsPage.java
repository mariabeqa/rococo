package org.rococo.page.artist;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.page.BasePage;
import org.rococo.page.component.Header;
import org.rococo.page.component.SearchField;
import org.rococo.page.component.artist.AddingArtistForm;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ArtistsPage extends BasePage<ArtistsPage> {

    public static final String URL = CFG.frontUrl() + "artist";

    protected final Header header = new Header();
    protected final SearchField searchField = new SearchField();
    private final SelenideElement title = $("main h2");
    private final SelenideElement addArtistBtn = $("main button.btn");
    private final SelenideElement grid = $("ul.grid");
    private final ElementsCollection artists = grid.$$("li");

    @Override
    public ArtistsPage checkThatPageLoaded() {
        title.should(visible).shouldHave(text("Художники"));
        return this;
    }

    @Step("Add an artist")
    @Nonnull
    public AddingArtistForm addArtist() {
        addArtistBtn.click();
        return new AddingArtistForm();
    }

    @Step("Search for an artist called '{0}'")
    @Nonnull
    public ArtistsPage searchArtist(String artistName) {
        searchField.search(artistName);
        return this;
    }

    @Step("Open an artist called '{0}'")
    @Nonnull
    public ArtistPage openArtist(String artistName) {
        searchArtist(artistName);
        artists.find(text(artistName)).click();
        return new ArtistPage();
    }

    @Step("Check that artist is found in the search results")
    @Nonnull
    public ArtistsPage checkArtistExists(String artistName) {
        searchArtist(artistName);
        artists.find(text(artistName)).should(visible);
        return this;
    }
}
