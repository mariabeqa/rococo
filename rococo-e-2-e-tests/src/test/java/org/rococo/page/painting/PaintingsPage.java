package org.rococo.page.painting;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.page.BasePage;
import org.rococo.page.component.painting.AddingPaintingForm;
import org.rococo.page.component.Header;
import org.rococo.page.component.SearchField;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PaintingsPage extends BasePage<PaintingsPage> {

    public static final String URL = CFG.frontUrl() + "painting";

    protected final Header header = new Header();
    protected final SearchField searchField = new SearchField();
    private final SelenideElement title = $("main h2");
    private final SelenideElement addPaintingBtn = $("main button.btn");
    private final SelenideElement grid = $("ul.grid");
    private final ElementsCollection paintings = grid.$$("div.text-center");
    private final String imageByText = "img[alt='%s']";

    @Override
    public PaintingsPage checkThatPageLoaded() {
        title.should(visible).shouldHave(text("Картины"));
        return this;
    }

    @Step("Add a painting")
    @Nonnull
    public AddingPaintingForm addPainting() {
        addPaintingBtn.click();
        return new AddingPaintingForm();
    }

    @Step("Search for a painting called '{0}'")
    @Nonnull
    public PaintingsPage searchPainting(String paintingName) {
        searchField.search(paintingName);
        return this;
    }

    @Step("Open a painting called '{0}'")
    @Nonnull
    public PaintingPage openPainting(String paintingName) {
        $(String.format(imageByText, paintingName)).click();

        return new PaintingPage();
    }

    @Step("Check that painting is found in the search results")
    @Nonnull
    public PaintingsPage checkPaintingExists(String paintingName) {
        searchPainting(paintingName);
        paintings.find(text(paintingName)).should(visible);
        return this;
    }
}
