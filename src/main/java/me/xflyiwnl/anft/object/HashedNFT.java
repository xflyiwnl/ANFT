package me.xflyiwnl.anft.object;

import me.xflyiwnl.anft.ANFT;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HashedNFT {

    private UUID uniqueId;
    private String address;
    private long createdMillis = System.currentTimeMillis();
    private List<BufferedNFT> nfts = new ArrayList<BufferedNFT>();

    public HashedNFT(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public HashedNFT(String address) {
        this.address = address;
    }

    public void create() {
        if (uniqueId != null) {
            ANFT.getInstance().getHashedUUIDMap().put(uniqueId, this);
        } else if (address != null) {
            ANFT.getInstance().getHashedNFTS().put(address, this);
        }
    }

    public void remove() {
        if (uniqueId != null) {
            ANFT.getInstance().getHashedUUIDMap().remove(uniqueId);
        } else if (address != null) {
            ANFT.getInstance().getHashedNFTS().remove(address);
        }
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCreatedMillis() {
        return createdMillis;
    }

    public void setCreatedMillis(long createdMillis) {
        this.createdMillis = createdMillis;
    }

    public List<BufferedNFT> getNfts() {
        return nfts;
    }

    public void setNfts(List<BufferedNFT> nfts) {
        this.nfts = nfts;
    }
}
