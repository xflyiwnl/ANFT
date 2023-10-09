package me.xflyiwnl.anft.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtil {

    /*
     *  Меняем размер изображения НФТ
     */

    public static BufferedImage resizeImage(Image image, int w, int h) {
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, w, h, null);
        graphics.dispose();
        return result;
    }

}
