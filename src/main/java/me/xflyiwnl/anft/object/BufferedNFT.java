package me.xflyiwnl.anft.object;

public class BufferedNFT {

    private String address;
    private String tokenId;
    private String name;
    private String description;
    private String url;

    public BufferedNFT(String address, String tokenId, String name, String description, String url) {
        this.address = address;
        this.tokenId = tokenId;
        this.name = name;
        this.description = description;
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
