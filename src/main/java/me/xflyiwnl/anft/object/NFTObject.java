package me.xflyiwnl.anft.object;

import java.util.UUID;

public abstract class NFTObject implements Scaleable {

    private String id = UUID.randomUUID().toString();
    private int w, h;

    public NFTObject() {
    }

    public NFTObject(String id) {
        this.id = id;
    }

    public NFTObject(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public NFTObject(String id, int w, int h) {
        this.id = id;
        this.w = w;
        this.h = h;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    @Override
    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
