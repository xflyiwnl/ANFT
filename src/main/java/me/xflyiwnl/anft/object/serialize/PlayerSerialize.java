package me.xflyiwnl.anft.object.serialize;

import me.xflyiwnl.anft.object.PlayerNFT;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerSerialize implements Serialize<PlayerNFT> {

    private PlayerNFT playerNFT;

    public PlayerSerialize() {
    }

    public PlayerSerialize(PlayerNFT playerNFT) {
        this.playerNFT = playerNFT;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("uniqueId", playerNFT.getUniqueId().toString());
        map.put("verified", playerNFT.isVerified());
        map.put("group", playerNFT.hasGroup() ? playerNFT.getGroup().getName() : "NOT HAVE GROUP");
        List<String> nfts = new ArrayList<String>();
        playerNFT.getNfts().forEach(nft -> {
            nfts.add(nft.getId());
        });
        map.put("nfts", nfts);

        return map;
    }

    @Override
    public Map<String, Object> deserialize(File file) {
        Map<String, Object> map = new HashMap<String, Object>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        if (!yaml.isConfigurationSection("player")) {
            return null;
        }

        for (String section : yaml.getConfigurationSection("player").getKeys(false)) {
            map.put(section, yaml.get("player." + section));
        }

        return map;
    }

}
