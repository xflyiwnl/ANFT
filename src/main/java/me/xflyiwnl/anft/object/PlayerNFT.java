package me.xflyiwnl.anft.object;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.ask.Ask;

import java.util.*;

public class PlayerNFT implements Identifyable {

    private UUID uniqueId;
    private boolean verified = false;
    private Map<String, NFT> nfts = new HashMap<String, NFT>();
    private List<Integer> rendered = new ArrayList<Integer>();
    private long cooldown = System.currentTimeMillis();

    private Ask ask;

    public PlayerNFT() {
    }

    public PlayerNFT(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public PlayerNFT(UUID uniqueId, boolean verified, Group group) {
        this.uniqueId = uniqueId;
        this.verified = verified;
    }

    public void create(boolean save) {
        ANFT.getInstance().getPlayers().put(uniqueId, this);
        if (save) save();
    }

    public void save() {
        ANFT.getInstance().getFlatFileSource().getPlayerData().save(this);
    }

    public void remove() {
        ANFT.getInstance().getFlatFileSource().getPlayerData().remove(this);
        ANFT.getInstance().getPlayers().remove(uniqueId);
    }

    public NFT getNFT(String id) {
        return nfts.get(id);
    }

    public boolean hasAsk() {
        return ask != null;
    }


    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<Integer> getRendered() {
        return rendered;
    }

    public void setRendered(List<Integer> rendered) {
        this.rendered = rendered;
    }

    public Ask getAsk() {
        return ask;
    }

    public void setAsk(Ask ask) {
        this.ask = ask;
    }

    public Map<String, NFT> getNfts() {
        return nfts;
    }

    public void setNfts(Map<String, NFT> nfts) {
        this.nfts = nfts;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}
