package me.xflyiwnl.anft.object;

import me.xflyiwnl.anft.ANFT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerNFT implements Identifyable {

    private UUID uniqueId;
    private List<Integer> rendered = new ArrayList<Integer>();

    public PlayerNFT() {
    }

    public PlayerNFT(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void create() {
        ANFT.getInstance().getPlayers().add(this);
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<Integer> getRendered() {
        return rendered;
    }

    public void setRendered(List<Integer> rendered) {
        this.rendered = rendered;
    }
}
