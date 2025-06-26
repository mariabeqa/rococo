package org.rococo.page.component;


import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Select extends BaseComponent<Select> {

    public Select(SelenideElement self) {
        super(self);
    }

    public void selectItem(String item) {
        List<String> allItemsLoaded = new ArrayList<>();
        String lastItemLoaded = "";


        while (!allItemsLoaded.contains(item)) {
            allItemsLoaded.clear();

            Selenide.sleep(2000);

            List<String> list = self.getOptions()
                    .stream()
                    .map(SelenideElement::getText)
                    .toList();

            String lastItem = list.getLast();
            if (lastItemLoaded.equals(lastItem)) {
                break;
            }
            lastItemLoaded = lastItem;
            allItemsLoaded.addAll(list);
            //Выбирает последний элемент, чтобы подгрузить следующие
            self.selectOption(allItemsLoaded.getLast());
        }

        //выбор нужного элемента
        self.selectOption(item);
    }
}
