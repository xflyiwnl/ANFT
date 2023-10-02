package me.xflyiwnl.anft.database.data;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.object.serialize.NFTSerialize;
import me.xflyiwnl.anft.util.ImageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NFTData implements Data<NFT> {

    @Override
    public void load() {
        List<NFT> nfts = all();
        for (NFT nft : nfts) {
            nft.getImage().load();
            nft.create(false);
            nft.locate();
        }
    }

    @Override
    public NFT get(File file) {
        NFT nft = new NFT();
        Map<String, Object> map = new NFTSerialize().deserialize(file);

        if (map.containsKey("owner"))
            nft.setOwner(UUID.fromString(map.get("owner").toString()));
        if (map.containsKey("id"))
            nft.setId(map.get("id").toString());
        if (map.containsKey("image"))
            nft.setImage(new ImageNFT(map.get("image").toString()));
        if (map.containsKey("point")) {
            String value = map.get("point").toString();
            if (!value.equalsIgnoreCase("null")) {
                String[] split = value.split(",");
                BlockFace face = BlockFace.valueOf(split[0].toString());
                Location location = new Location(
                        Bukkit.getWorld(split[1]),
                        Double.valueOf(split[2]),
                        Double.valueOf(split[3]),
                        Double.valueOf(split[4])
                );
                nft.setPoint(new Point(location, face));
            }
        }
        nft.setPlaced(Boolean.valueOf(map.get("placed").toString()));
        if (map.containsKey("w"))
        if (map.containsKey("placed"))
            nft.setW(Integer.valueOf(map.get("w").toString()));
        if (map.containsKey("h"))
            nft.setH(Integer.valueOf(map.get("h").toString()));
        if (map.containsKey("tokenId"))
            nft.setTokenId(Integer.valueOf(map.get("tokenId").toString()));
        if (map.containsKey("name"))
            nft.setName(map.get("name").toString());
        if (map.containsKey("description"))
            nft.setDescription(map.get("description").toString());

        File imageFile = new File(ANFT.getInstance().getFileManager().getImageFolder().getPath(), nft.getId() + ".png");
        if (!imageFile.exists()) {
            return null;
        }
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        nft.getImage().setImage(ImageUtil.resizeImage(bufferedImage, nft.getW(), nft.getH()));

        if (map.containsKey("figures")) {
//            List<String> formatted = (List<String>) map.get("figures");
//            if (!formatted.isEmpty()) {
//                for (String value : formatted) {
//                    String[] split = value.split(",");
//                    nft.getFigures().add(new Figure(
//                            split[0],
//                            Integer.valueOf(split[1]),
//                            Integer.valueOf(split[2]),
//                            Integer.valueOf(split[3]),
//                            Integer.valueOf(split[4])
//                    ));
//                }
//            }
            nft.frames();
            nft.getFigures().forEach(figure -> {
                System.out.println(figure.getId() + " / " + figure.getH() + " / " + figure.getW() + " / " + figure.getOw() + " / " + figure.getOh());
            });
        }

        return nft;
    }

    @Override
    public NFT get(UUID uniqueId) {
        return get(new File(ANFT.getInstance().getFileManager().getNftsFolder().getPath(), File.separator + uniqueId.toString() + ".yml"));
    }

    @Override
    public void save(NFT nft) {

        if (!exists(nft.getId())) {
            create(nft);
        }

        try {
            ImageIO.write(nft.getImage().getImage(), "png", new File(ANFT.getInstance().getFileManager().getImageFolder().getPath(), nft.getId() + ".png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File file = new File(ANFT.getInstance().getFileManager().getNftsFolder().getPath(), File.separator + nft.getId() + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        Map<String, Object> serialize = new NFTSerialize(nft).serialize();

        for (String key : serialize.keySet()) {
            Object value = serialize.get(key);
            yaml.set("nft." + key, value);
        }

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(NFT nft) {
        File file = new File(ANFT.getInstance().getFileManager().getNftsFolder().getPath(), File.separator + nft.getId() + ".yml");
        file.delete();
    }

    @Override
    public List<NFT> all() {
        List<NFT> nfts = new ArrayList<NFT>();

        File folder = ANFT.getInstance().getFileManager().getNftsFolder();
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) continue;
            NFT nft = get(file);
            if (nft == null) continue;
            nfts.add(nft);
        }

        return nfts;
    }

    @Override
    public void create(NFT nft) {
        File file = new File(ANFT.getInstance().getFileManager().getNftsFolder().getPath(), File.separator + nft.getId() + ".yml");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean exists(Object id) {
        File file = new File(ANFT.getInstance().getFileManager().getNftsFolder().getPath(), id.toString() + ".yml");
        return file.exists();
    }

}
