package me.xflyiwnl.anft.object.serialize;

import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.Point;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NFTSerialize implements Serialize<NFT> {

    private NFT nft;

    public NFTSerialize() {
    }

    public NFTSerialize(NFT nft) {
        this.nft = nft;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("owner", nft.getOwner().toString());
        map.put("id", nft.getId());
        map.put("name", nft.getName());
        map.put("description", nft.getDescription());
        map.put("tokenId", nft.getTokenId());
        map.put("image", nft.getImage().getUrl());

        Point point = nft.getPoint();
        if (point != null) {
            Location ploc = point.getLocation();
            map.put("point", point.getFace().toString() + "," + ploc.getWorld().getName() + "," + ploc.getX() + "," + ploc.getY() + "," + ploc.getZ());
        } else {
            map.put("point", "null");
        }

        map.put("placed", nft.isPlaced());

        List<Figure> figures = nft.getFigures();
        List<String> formattedFig = new ArrayList<String>();
        for (Figure figure : figures) {
            formattedFig.add(
                    figure.getId() + "," + figure.getW() + "," + figure.getH() + "," + figure.getOw() + "," + figure.getOh()
            );
        }
        map.put("figures", formattedFig);
        map.put("w", nft.getW());
        map.put("h", nft.getH());

        return map;
    }

    @Override
    public Map<String, Object> deserialize(File file) {
        Map<String, Object> map = new HashMap<String, Object>();
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        if (!yaml.isConfigurationSection("nft")) {
            return null;
        }

        for (String section : yaml.getConfigurationSection("nft").getKeys(false)) {
            map.put(section, yaml.get("nft." + section));
        }

        return map;
    }
}
