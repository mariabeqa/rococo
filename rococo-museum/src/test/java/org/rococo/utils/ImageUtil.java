package org.rococo.utils;


import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@ParametersAreNonnullByDefault
public class ImageUtil {

    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final String RESULT = "data:image/%s;base64,%s";

    public static String getEncodedImageFromClasspath(String imageClasspath) {
        final String fileExtension = imageClasspath.substring(imageClasspath.lastIndexOf(".") + 1);
        ClassLoader classLoader = ImageUtil.class.getClassLoader();

        try (InputStream is = classLoader.getResourceAsStream(imageClasspath)) {
            assert is != null;
            byte[] encodedImage = ENCODER.encode(is.readAllBytes());

            return String.format(RESULT, fileExtension, new String(encodedImage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
