package me.xflyiwnl.anft.object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageNFT {

    private BufferedImage image;
    private String url;

    public ImageNFT() {
    }

    public ImageNFT(String url) {
        this.url = url;
    }

    public ImageNFT(BufferedImage image, String url) {
        this.image = image;
        this.url = url;
    }

    public boolean load() {
        try {
            image = ImageIO.read(new URL(url));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public ImageNFT crop(int ow, int oh) {
        image = image.getSubimage(ow, oh, 128, 128);
        return this;
    }

    public ImageNFT clone() {
        return new ImageNFT(image, url);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
