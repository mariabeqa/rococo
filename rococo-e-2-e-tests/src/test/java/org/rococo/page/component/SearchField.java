package org.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

  public SearchField() {
    super($("main div.justify-center"));
  }

  private final SelenideElement searchInput = self.$(".input");
  private final SelenideElement searchBtn = $("button");

  @Nonnull
  @Step("Search for '{0}'")
  public SearchField search(String query) {
    clearIfNotEmpty();
    searchInput.setValue(query).pressEnter();
    return this;
  }

  @Nonnull
  @Step("Fill in search field '{0}'")
  public SearchField enterSearchTerm(String query) {
    clearIfNotEmpty();
    searchInput.setValue(query);
    return this;
  }

  @Nonnull
  @Step("Press 'Search' button")
  public SearchField pressSearchButton() {
    searchBtn.click();
    return this;
  }

  @Nonnull
  @Step("Press 'Enter' button")
  public SearchField pressEnterButton() {
    self.pressEnter();
    return this;
  }

  @Nonnull
  @Step("Clear search field")
  public SearchField clearIfNotEmpty() {
    if (self.is(not(empty))) {
      searchInput.click();
      searchInput.clear();
      self.should(empty);
    }
    return this;
  }
}
