package me.xflyiwnl.anft.database;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.FileManager;
import me.xflyiwnl.anft.database.data.NFTData;
import me.xflyiwnl.anft.database.data.PlayerData;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.PlayerNFT;

public class FlatFileSource implements DataSource {

    private FileManager manager;
    private PlayerData playerData = new PlayerData();
    private NFTData nftData = new NFTData();

    @Override
    public void load() {
        this.manager = ANFT.getInstance().getFileManager();

        playerData.load();
        nftData.load();

        ANFT.getInstance().getNfts().forEach(nft -> {
            PlayerNFT playerNFT = ANFT.getInstance().getPlayer(nft.getOwner());
            if (playerNFT == null) {
                return;
            }
            playerNFT.getNfts().add(nft);
        });

        ANFT.getInstance().checkPlayers();

    }

    @Override
    public void unload() {
        ANFT.getInstance().getNfts().forEach(NFT::save);
        ANFT.getInstance().getPlayers().forEach(PlayerNFT::save);
    }

    public FileManager getManager() {
        return manager;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public NFTData getNftData() {
        return nftData;
    }
}
