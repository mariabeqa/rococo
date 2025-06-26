package org.rococo.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {

  public static final Object LOCK = new Object();

  protected final SelenideElement self;

  protected BaseComponent(SelenideElement self) {
    this.self = self;
  }

  @Nonnull
  public SelenideElement getSelf() {
    return self;
  }

}
