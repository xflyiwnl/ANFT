package me.xflyiwnl.anft.object.nft;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImageNFT {

    private File image;
    private String url;
    private boolean imageLoaded = false;

    public ImageNFT() {
    }

    public ImageNFT(String url) {
        this.url = url;
    }

    public ImageNFT(File image, String url) {
        this.image = image;
        this.url = url;
    }

    public BufferedImage load() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
        return image;
    }

    public BufferedImage loadFromStorage() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(this.image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    public ImageNFT clone() {
        return new ImageNFT(image, url);
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public boolean isImageLoaded() {
        return imageLoaded;
    }

    public void setImageLoaded(boolean imageLoaded) {
        this.imageLoaded = imageLoaded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
