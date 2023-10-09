package me.xflyiwnl.anft.object.nft;

import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.NFTObject;
import me.xflyiwnl.anft.object.Scaleable;

import java.util.UUID;

public class Figure extends NFTObject implements Scaleable {

    private NFT nft;
    private int mapId = -1;
    private int ow = 0, oh = 0;

    public Figure() {
    }

    public Figure(int w, int h, int ow, int oh) {
        super(w, h);
        this.ow = ow;
        this.oh = oh;
    }

    public Figure(String id, int w, int h) {
        super(id, w, h);
    }

    public Figure(NFT nft, int mapId, String id, int w, int h, int ow, int oh) {
        super(id, w, h);
        this.nft = nft;
        this.mapId = mapId;
        this.ow = ow;
        this.oh = oh;
    }

    public NFT getNft() {
        return nft;
    }

    public void setNft(NFT nft) {
        this.nft = nft;
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

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }
}
