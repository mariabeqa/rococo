package org.rococo.page.museum;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.page.BasePage;
import org.rococo.page.component.Header;
import org.rococo.page.component.SearchField;
import org.rococo.page.component.museum.AddingMuseumForm;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MuseumsPage extends BasePage<MuseumsPage> {

    public static final String URL = CFG.frontUrl() + "museum";

    protected final Header header = new Header();
    protected final SearchField searchField = new SearchField();
    private final SelenideElement title = $("main h2");
    private final SelenideElement addMuseumBtn = $("main button.btn");
    private final SelenideElement grid = $("ul.grid");
    private final ElementsCollection museums = grid.$$("li");

    @Override
    public MuseumsPage checkThatPageLoaded() {
        title.should(visible).shouldHave(text("Музеи"));
        return this;
    }

    @Step("Add a museum")
    @Nonnull
    public AddingMuseumForm addMuseum() {
        addMuseumBtn.click();
        return new AddingMuseumForm();
    }

    @Step("Search for a museum called '{0}'")
    @Nonnull
    public MuseumsPage searchMuseum(String museumName) {
        searchField.search(museumName);
        return this;
    }

    @Step("Check that museum is found in the search results")
    @Nonnull
    public MuseumsPage checkMuseumExists(String museumName) {
        searchMuseum(museumName);
        museums.find(text(museumName)).should(visible);
        return this;
    }

    @Step("Open a museum called '{0}'")
    @Nonnull
    public MuseumPage openMuseum(String museumName) {
        searchMuseum(museumName);
        museums.find(text(museumName)).click();
        return new MuseumPage();
    }
}
