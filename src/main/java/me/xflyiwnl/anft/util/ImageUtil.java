package me.xflyiwnl.anft.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtil {

    /*
     *  Меняем размер изображения НФТ
     */

    public static BufferedImage resizeImage(BufferedImage image, int w, int h) {
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, w, h, null);
        graphics.dispose();
        return result;
    }

    public static String getFormattedSize(String formattedUrl) {
        URL url = null;
        int width = 0;
        int height = 0;
        try {
            url = new URL(formattedUrl);
        } catch (MalformedURLException e) {
            return null;
        }
        try (InputStream stream = url.openStream()) {
            try (ImageInputStream input = ImageIO.createImageInputStream(stream)) {
                ImageReader reader = ImageIO.getImageReaders(input).next(); // TODO: Handle no reader
                try {
                    reader.setInput(input);

                    width = reader.getWidth(0);
                    height = reader.getHeight(0);
                }
                finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            return null;
        }
        return width + "x" + height;
    }

    public static BufferedImage crop(BufferedImage image, int ow, int oh) {
        return image.getSubimage(ow, oh, 128, 128);
    }

}
