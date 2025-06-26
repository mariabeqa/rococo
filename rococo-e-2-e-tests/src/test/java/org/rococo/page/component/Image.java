package org.rococo.page.component;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.rococo.jupiter.extension.ScreenShotTestExtension;
import org.rococo.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.attributeMatching;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Image extends BaseComponent<Image> {

    public Image(SelenideElement element) {
        super(element);
    }

    public void checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(self.screenshot()));
        assertFalse(
                new ScreenDiffResult(
                        actualImage, expected
                ),
                ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
        );
    }

    public void checkPhotoExist() {
        self.should(attributeMatching("src", "data:image.*"));
    }
}
