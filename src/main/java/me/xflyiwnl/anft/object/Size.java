package me.xflyiwnl.anft.object;

public class Size {

    private int w = 1, h = 1;

    public Size(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public String formatted() {
        return w + "x" + h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
