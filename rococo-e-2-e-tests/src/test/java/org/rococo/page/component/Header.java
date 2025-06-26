package org.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.rococo.page.*;
import org.rococo.page.artist.ArtistsPage;
import org.rococo.page.museum.MuseumsPage;
import org.rococo.page.painting.PaintingsPage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

  public Header() {
    super($("div[data-testid='app-bar']"));
  }

  private final SelenideElement mainPageIcon = self.$("a[href*='/']");
  private final SelenideElement profileBtn = self.$("button figure.avatar");
  private final SelenideElement paintingTab = self.$("a[href='/painting']");
  private final SelenideElement artistTab = self.$("a[href='/artist']");
  private final SelenideElement museumTab = self.$("a[href='/museum']");
  private final SelenideElement lightSwitch = self.$("div.lightswitch-track");
  private final SelenideElement loginBtn = self.$("button.variant-filled-primary");

  @Step("Open Main page")
  @Nonnull
  public MainPage toMainPage() {
    mainPageIcon.click();
    return new MainPage();
  }

  @Step("Open Paintings page")
  @Nonnull
  public PaintingsPage toPaintingsPage() {
    paintingTab.click();
    return new PaintingsPage();
  }

  @Step("Open Artists page")
  @Nonnull
  public ArtistsPage toArtistsPage() {
    artistTab.click();
    return new ArtistsPage();
  }

  @Step("Open Museums page")
  @Nonnull
  public MuseumsPage toMuseumsPage() {
    museumTab.click();
    return new MuseumsPage();
  }

  @Step("Open Profile")
  @Nonnull
  public Profile profile() {
    profileBtn.click();
    return new Profile();
  }

  @Step("Switch theme")
  @Nonnull
  public <T extends BasePage<?>> T switchLight(T page) {
    lightSwitch.click();
    return page;
  }

  @Step("Log in")
  @Nonnull
  public LoginPage logIn() {
    loginBtn.click();
    return new LoginPage();
  }
}
