package me.xflyiwnl.anft.object.nft;

import me.xflyiwnl.anft.object.NFTObject;
import me.xflyiwnl.anft.object.Scaleable;

import java.util.UUID;

public class Figure extends NFTObject implements Scaleable {

    private int ow = 0, oh = 0;

    public Figure() {
    }

    public Figure(int w, int h, int ow, int oh) {
        super(w, h);
        this.ow = ow;
        this.oh = oh;
    }

    public Figure(UUID uniqueId, int w, int h) {
        super(uniqueId, w, h);
    }

    public Figure(UUID uniqueId, int w, int h, int ow, int oh) {
        super(uniqueId, w, h);
        this.ow = ow;
        this.oh = oh;
    }

    public int getOw() {
        return ow;
    }

    public void setOw(int ow) {
        this.ow = ow;
    }

    public int getOh() {
        return oh;
    }

    public void setOh(int oh) {
        this.oh = oh;
    }
}
