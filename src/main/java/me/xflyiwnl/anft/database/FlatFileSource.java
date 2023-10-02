package me.xflyiwnl.anft.database;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.FileManager;
import me.xflyiwnl.anft.database.data.NFTData;
import me.xflyiwnl.anft.database.data.PlayerData;

import java.io.File;

public class FlatFileSource implements DataSource {

    private FileManager manager;
    private PlayerData playerData = new PlayerData();
    private NFTData nftData = new NFTData();

    @Override
    public void load() {
        this.manager = ANFT.getInstance().getFileManager();

        playerData.load();
        nftData.load();

    }

    @Override
    public void unload() {

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
