package me.xflyiwnl.anft.object;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.ask.Ask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerNFT implements Identifyable {

    private UUID uniqueId;
    private boolean verified = false;
    private Group group;
    private List<NFT> nfts = new ArrayList<NFT>();
    private List<Integer> rendered = new ArrayList<Integer>();
    private long cooldown = System.currentTimeMillis();

    private Ask ask;

    public PlayerNFT() {
    }

    public PlayerNFT(UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.group = ANFT.getInstance().getDefaultGroup();
    }

    public PlayerNFT(UUID uniqueId, boolean verified, Group group) {
        this.uniqueId = uniqueId;
        this.verified = verified;
        this.group = group;
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
        for (NFT nft : nfts) {
            if (nft.getId().equalsIgnoreCase(id)) {
                return nft;
            }
        }
        return null;
    }

    public boolean hasAsk() {
        return ask != null;
    }

    public boolean hasGroup() {return group != null;}

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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

    public List<NFT> getNfts() {
        return nfts;
    }

    public void setNfts(List<NFT> nfts) {
        this.nfts = nfts;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}
