package me.xflyiwnl.anft;

import me.xflyiwnl.anft.command.NFTCommand;
import me.xflyiwnl.anft.database.FlatFileSource;
import me.xflyiwnl.anft.listener.MapListener;
import me.xflyiwnl.anft.listener.PlayerListener;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ANFT extends JavaPlugin {

    private static ANFT instance;

    private FileManager fileManager = new FileManager();
    private FlatFileSource flatFileSource = new FlatFileSource();
    private NamespacedKey key = new NamespacedKey(this, "anft");

    private List<NFT> nfts = new ArrayList<NFT>();
    private List<PlayerNFT> players = new ArrayList<PlayerNFT>();

    @Override
    public void onEnable() {
        instance = this;

        fileManager.generate();
        flatFileSource.load();

        registerCommand();
        registerListener();

        checkPlayers();
    }

    @Override
    public void onDisable() {
        flatFileSource.unload();
    }

    public void registerCommand() {
        getCommand("anft").setExecutor(new NFTCommand());
    }

    public void registerListener () {
        Bukkit.getPluginManager().registerEvents(new MapListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public void checkPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
            if (playerNFT == null) {
                playerNFT = new PlayerNFT(player.getUniqueId());
                playerNFT.create();
            }
        }
    }

    public NFT getNFT(UUID uniqueId) {
        for (NFT nft : nfts) {
            if (nft.getUniqueId().equals(uniqueId)) {
                return nft;
            }
        }
        return null;
    }

    public PlayerNFT getPlayer(UUID uniqueId) {
        for (PlayerNFT player : players) {
            if (player.getUniqueId().equals(uniqueId)) {
                return player;
            }
        }
        return null;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public List<PlayerNFT> getPlayers() {
        return players;
    }

    public List<NFT> getNfts() {
        return nfts;
    }

    public FlatFileSource getFlatFileSource() {
        return flatFileSource;
    }

    public static ANFT getInstance() {
        return instance;
    }

}
