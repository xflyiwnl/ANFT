package me.xflyiwnl.anft.database.data;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.Group;
import me.xflyiwnl.anft.object.PlayerNFT;
import me.xflyiwnl.anft.object.serialize.PlayerSerialize;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerData implements Data<PlayerNFT> {

    @Override
    public void load() {
        List<PlayerNFT> players = all();
        for (PlayerNFT player : players) {
            player.create(false);
        }
    }

    @Override
    public PlayerNFT get(File file) {
        PlayerNFT player = new PlayerNFT();
        Map<String, Object> map = new PlayerSerialize().deserialize(file);

        if (map.containsKey("uniqueId"))
            player.setUniqueId(UUID.fromString(map.get("uniqueId").toString()));

        if (map.containsKey("verified"))
            player.setVerified(Boolean.valueOf(map.get("verified").toString()));

        return player;
    }

    @Override
    public PlayerNFT get(UUID uniqueId) {
        return get(new File(ANFT.getInstance().getFileManager().getPlayersFolder().getPath(), File.separator + uniqueId.toString() + ".yml"));
    }

    @Override
    public List<PlayerNFT> all() {
        List<PlayerNFT> players = new ArrayList<PlayerNFT>();

        File folder = ANFT.getInstance().getFileManager().getPlayersFolder();

        if (folder == null) return players;
        if (folder.listFiles() == null) return players;
        for (File file : folder.listFiles()) {
            PlayerNFT player = get(file);
            if (player == null) continue;
            players.add(player);
        }

        return players;
    }

    @Override
    public void save(PlayerNFT playerNFT) {

        if (!exists(playerNFT.getUniqueId())) {
            create(playerNFT);
        }

        File file = new File(ANFT.getInstance().getFileManager().getPlayersFolder().getPath(), File.separator + playerNFT.getUniqueId().toString() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        Map<String, Object> serialize = new PlayerSerialize(playerNFT).serialize();

        for (String key : serialize.keySet()) {
            Object value = serialize.get(key);
            yaml.set("player." + key, value);
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(PlayerNFT playerNFT) {
        File file = new File(ANFT.getInstance().getFileManager().getPlayersFolder().getPath(), File.separator + playerNFT.getUniqueId().toString() + ".yml");
        file.delete();
    }

    @Override
    public void create(PlayerNFT playerNFT) {
        File file = new File(ANFT.getInstance().getFileManager().getPlayersFolder().getPath(), File.separator + playerNFT.getUniqueId().toString() + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean exists(Object uniqueId) {
        File file = new File(ANFT.getInstance().getFileManager().getPlayersFolder().getPath(), uniqueId.toString() + ".yml");
        return file.exists();
    }

}
