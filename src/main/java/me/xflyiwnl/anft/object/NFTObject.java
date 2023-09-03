package me.xflyiwnl.anft.object;

import java.util.UUID;

public abstract class NFTObject implements Identifyable, Scaleable {

    private UUID uniqueId = UUID.randomUUID();
    private int w, h;

    public NFTObject() {
    }

    public NFTObject(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public NFTObject(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public NFTObject(UUID uniqueId, int w, int h) {
        this.uniqueId = uniqueId;
        this.w = w;
        this.h = h;
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
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
