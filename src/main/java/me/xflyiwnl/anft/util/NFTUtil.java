package me.xflyiwnl.anft.util;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.BufferedNFT;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import me.xflyiwnl.anft.object.Size;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.render.NFTRenderer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NFTUtil {

    public static NFT getNFTfromMap(ItemStack itemStack) {
        if (itemStack.getType() != Material.FILLED_MAP) {
            return null;
        }
        MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(ANFT.getInstance().getKey(), PersistentDataType.STRING)) {
            return null;
        }
        String uniqueId = container.get(ANFT.getInstance().getKey(), PersistentDataType.STRING);
        NFT nft = ANFT.getInstance().getNFT(uniqueId);
        if (nft == null) {
            return null;
        }
        return nft;
    }

    /*
     *  Проверяем содержит ли рамка НФТ
     */

    public static boolean isNFT(ItemFrame frame) {
        ItemStack itemStack = frame.getItem();

        if (itemStack.getType() != Material.FILLED_MAP) {
            return false;
        }

        MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(ANFT.getInstance().getKey(), PersistentDataType.STRING)) {
            return false;
        }
        String uniqueId = container.get(ANFT.getInstance().getKey(), PersistentDataType.STRING);
        NFT nft = ANFT.getInstance().getNFT(uniqueId);
        if (nft == null) {
            return false;
        }
        return true;
    }

    /*
     *  Проверяем содержит ли массив нфт
     */

    public static boolean containsNFT(List<ItemFrame> frames) {
        for (ItemFrame frame : frames) {
            ItemStack itemStack = frame.getItem();

            if (itemStack.getType() != Material.FILLED_MAP) {
                continue;
            }

            MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
            if (itemMeta == null) {
                continue;
            }
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (!container.has(ANFT.getInstance().getKey(), PersistentDataType.STRING)) {
                continue;
            }
            String uniqueId = container.get(ANFT.getInstance().getKey(), PersistentDataType.STRING);
            NFT nft = ANFT.getInstance().getNFT(uniqueId);
            if (nft == null) {
                continue;
            }
            return true;
        }
        return false;
    }

    /*
     *  Выдаём нфт как предмет
     *  Если нфт уже загружен и существует, то выдаём существующий
     *  Если нфт не существует, то создаём и загружаем картинку
     */

    public static void giveNFT(Player player, BufferedNFT bufferedNFT, Size size) {

        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());

        new MessageSender(player)
                .path("nft-loading")
                .run();

        String formattedId = generateId(bufferedNFT.getAddress(), bufferedNFT.getTokenId());
        NFT snft = ANFT.getInstance().getNFT(formattedId);
        if (snft != null) {

            new BukkitRunnable() {

                @Override
                public void run() {

                    if (size.getW() * 128 != snft.getW()
                            && size.getH() * 128 != snft.getH()) {
//                        snft.getImage().setImage(ImageUtil.resizeImage(image, size.getW() * 128, size.getH() * 128));
                        snft.setW(size.getW() * 128);
                        snft.setH(size.getH() * 128);
                        snft.frames();
                        snft.save();
                    }

                    player.getInventory().addItem(snft.asItemStack(player.getWorld()));

                    new MessageSender(player)
                            .path("given-nft")
                            .run();
                }

            }.runTaskAsynchronously(ANFT.getInstance());

            return;
        }


        new BukkitRunnable() {

            @Override
            public void run() {

                ImageNFT imageNFT = new ImageNFT(bufferedNFT.getUrl());
                BufferedImage image = imageNFT.load();

                if (image == null) {
                    new MessageSender(player)
                            .path("response-error")
                            .replace("code", "?")
                            .replace("description", "???")
                            .run();
                    return;
                }

                NFT nft = new NFT(formattedId, size.getW() * 128, size.getH() * 128, bufferedNFT.getTokenId(), bufferedNFT.getName(), bufferedNFT.getDescription(), player.getUniqueId(), imageNFT);
                nft.frames();
                nft.create(true);

                File path = new File(ANFT.getInstance().getFileManager().getImageFolder().getPath(), nft.getId() + ".png");
                try {
                    ImageIO.write(image, "png", path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                nft.getImage().setImage(path);

                playerNFT.getNfts().add(nft);
                playerNFT.save();


                new BukkitRunnable() {

                    @Override
                    public void run() {
                        player.getInventory().addItem(nft.asItemStack(player.getWorld()));
                    }

                }.runTask(ANFT.getInstance());

                new MessageSender(player)
                        .path("given-nft")
                        .run();

            }

        }.runTaskAsynchronously(ANFT.getInstance());

    }

    /*
     *  Выдача существующего нфт игроку
     */

    public static void giveNFT(Player player, NFT nft, Size size) {

        new MessageSender(player)
                .path("nft-loading")
                .run();

        if (size.getW() * 128 != nft.getW()
                && size.getH() * 128 != nft.getH()) {
//            nft.getImage().setImage(ImageUtil.resizeImage(nft.getImage().getImage(), size.getW() * 128, size.getH() * 128));
            nft.setW(size.getW() * 128);
            nft.setH(size.getH() * 128);
            nft.frames();
            nft.save();
        }

        player.getInventory().addItem(nft.asItemStack(player.getWorld()));

        new MessageSender(player)
                .path("given-nft")
                .run();

    }

    /*
     *  Генерация айди из адреса и токена
     */

    public static String generateId(String address, int tokenId) {
        return address + "-" + tokenId;
    }

    /*
     *  Проверяем может ли находиться в рамке нфт
     */

    public static boolean checkFrame(Location location, BlockFace face, double x, double y, double z) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        ItemFrame frame = FrameUtil.getFrame(resultLocation, face);

        if (frame == null) {
            return true;
        }

        if (frame.getItem().getType() != Material.AIR) {
            return true;
        }

        return false;
    }

    /*
     *  Проверяем все соседние рамки от основного на способность
     *  содержания части НФТ (фигура)
     */

    public static boolean checkFrames(Location location, NFT nft,
                                      BlockFace face, Orient orient) {
        double x, y, z;
        switch (orient.getSide()) {
            case Z_SIDE_INVERTED:
            case Z_SIDE:
                x = location.getX();
                for (z = orient.getZa(); z <= orient.getZb(); z++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        if (checkFrame(location, face, x, y, z)) {
                            return false;
                        }
                    }
                }
                break;
            case UP:
            case DOWN:
                y = location.getY();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (z = orient.getZa(); z >= orient.getZb(); z--) {
                        if (checkFrame(location, face, x, y, z)) {
                            return false;
                        }
                    }
                }
                break;
            case X_SIDE:
            case X_SIDE_INVERTED:
                z = location.getZ();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        if (checkFrame(location, face, x, y, z)) {
                            return false;
                        }
                    }
                }
                break;
        }
        return true;
    }

    /*
     *  Создаём рендер и ставим часть НФТ (фигура) в рамку
     */

    public static void populateFrame(BufferedImage image, World world, Location location, NFT nft, BlockFace face, Orient orient, double x, double y, double z, int fw, int fh) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        ItemFrame frame = FrameUtil.getFrame(resultLocation, face);

        Figure figure = nft.getFigure(fw * 128, fh * 128);

        NFTRenderer renderer = new NFTRenderer(ImageUtil.crop(image, fw * 128, fh * 128));

        MapView view = Bukkit.createMap(world);
        view.getRenderers().clear();
        view.addRenderer(renderer);

        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapView(view);
        PersistentDataContainer container = mapMeta.getPersistentDataContainer();
        container.set(ANFT.getInstance().getKey(), PersistentDataType.STRING, nft.getId());
        itemStack.setItemMeta(mapMeta);
        figure.setMapId(view.getId());

        frame.setItem(itemStack);
    }

    /*
     *  Устанавливаем НФТ в рамки (выполняется после проверки)
     */

    public static void populateFrames(World world, Location location, NFT nft, BlockFace face, Orient orient) {
        BufferedImage image = ImageUtil.resizeImage(nft.getImage().loadFromStorage(), nft.getW(), nft.getH());
        int fw = 0, fh = 0;
        double x, y, z;
        switch (orient.getSide()) {
            case Z_SIDE:
            case Z_SIDE_INVERTED:
                fw = orient.getSide() == OrientSide.Z_SIDE_INVERTED ? (nft.getW() / 128 - 1) : 0;
                x = location.getX();
                for (z = orient.getZa(); z <= orient.getZb(); z++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        populateFrame(image, world, location, nft, face, orient, x, y, z, fw, fh);
                        if (nft.getH() / 128 - 1 == fh) {
                            fh = 0;
                        } else {
                            fh++;
                        }
                    }
                    if (orient.getSide() == OrientSide.Z_SIDE_INVERTED) {
                        fw--;
                    } else fw++;
                }
                break;
            case DOWN:
            case UP:
                fh = orient.getSide() == OrientSide.UP ? (nft.getH() / 128 - 1) : 0;
                y = location.getY();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (z = orient.getZa(); z >= orient.getZb(); z--) {
                        populateFrame(image, world, location, nft, face, orient, x, y, z, fw, fh);

                        if (orient.getSide() == OrientSide.UP && fh == 0) {
                            fh = nft.getH() / 128 - 1;
                        } else if (orient.getSide() == OrientSide.DOWN && fh == (nft.getH() / 128 - 1)) {
                            fh = 0;
                        } else {
                            if (orient.getSide() == OrientSide.UP) {
                                fh--;
                            } else fh++;
                        }
                    }
                    fw++;
                }
                break;
            case X_SIDE:
            case X_SIDE_INVERTED:
                fw = orient.getSide() == OrientSide.X_SIDE_INVERTED ? (nft.getW() / 128 - 1) : 0;
                z = location.getZ();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        populateFrame(image, world, location, nft, face, orient, x, y, z, fw, fh);
                        if (nft.getH() / 128 - 1 == fh) {
                            fh = 0;
                        } else {
                            fh++;
                        }
                    }
                    if (orient.getSide() == OrientSide.X_SIDE_INVERTED) {
                        fw--;
                    } else fw++;
                }
                break;
        }
    }

    public static void loadFrame(BufferedImage image, World world, Location location, NFT nft, BlockFace face, Orient orient, double x, double y, double z, int fw, int fh) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Location resultLocation = new Location(location.getWorld(), x, y, z);
                Chunk chunk = resultLocation.getChunk();
                if (!chunk.isLoaded()) {
                    ANFT.getInstance().getChunks().add(chunk);
                    chunk.load();
                }

                ItemFrame frame = FrameUtil.getFrame(resultLocation, face);
                Figure figure = nft.getFigure(fw * 128, fh * 128);

                NFTRenderer renderer = new NFTRenderer(ImageUtil.crop(image, fw * 128, fh * 128));

                MapView view = Bukkit.createMap(world);
                view.getRenderers().clear();
                view.addRenderer(renderer);

                ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
                MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                mapMeta.setMapView(view);
                PersistentDataContainer container = mapMeta.getPersistentDataContainer();
                container.set(ANFT.getInstance().getKey(), PersistentDataType.STRING, nft.getId());
                itemStack.setItemMeta(mapMeta);
                figure.setMapId(view.getId());

                frame.setItem(itemStack);

                ANFT.getInstance().getChunks().remove(chunk);
            }
        }.runTask(ANFT.getInstance());
    }

    public static void loadFrames(World world, Location location, NFT nft, BlockFace face, Orient orient) {
        BufferedImage image = ImageUtil.resizeImage(nft.getImage().loadFromStorage(), nft.getW(), nft.getH());
        int fw = 0, fh = 0;
        double x, y, z;
        switch (orient.getSide()) {
            case Z_SIDE:
            case Z_SIDE_INVERTED:
                fw = orient.getSide() == OrientSide.Z_SIDE_INVERTED ? (nft.getW() / 128 - 1) : 0;
                x = location.getX();
                for (z = orient.getZa(); z <= orient.getZb(); z++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        loadFrame(image, world, location, nft, face, orient, x, y, z, fw, fh);
                        if (nft.getH() / 128 - 1 == fh) {
                            fh = 0;
                        } else {
                            fh++;
                        }
                    }
                    if (orient.getSide() == OrientSide.Z_SIDE_INVERTED) {
                        fw--;
                    } else fw++;
                }
                break;
            case DOWN:
            case UP:
                fh = orient.getSide() == OrientSide.UP ? (nft.getH() / 128 - 1) : 0;
                y = location.getY();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (z = orient.getZa(); z >= orient.getZb(); z--) {
                        loadFrame(image, world, location, nft, face, orient, x, y, z, fw, fh);

                        if (orient.getSide() == OrientSide.UP && fh == 0) {
                            fh = nft.getH() / 128 - 1;
                        } else if (orient.getSide() == OrientSide.DOWN && fh == (nft.getH() / 128 - 1)) {
                            fh = 0;
                        } else {
                            if (orient.getSide() == OrientSide.UP) {
                                fh--;
                            } else fh++;
                        }
                    }
                    fw++;
                }
                break;
            case X_SIDE:
            case X_SIDE_INVERTED:
                fw = orient.getSide() == OrientSide.X_SIDE_INVERTED ? (nft.getW() / 128 - 1) : 0;
                z = location.getZ();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        loadFrame(image, world, location, nft, face, orient, x, y, z, fw, fh);
                        if (nft.getH() / 128 - 1 == fh) {
                            fh = 0;
                        } else {
                            fh++;
                        }
                    }
                    if (orient.getSide() == OrientSide.X_SIDE_INVERTED) {
                        fw--;
                    } else fw++;
                }
                break;
        }
    }

    /*
     *  Очищаем рамку
     */

    public static void clearFrame(ItemFrame frame, Location location, double x, double y, double z) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        ItemFrame fr = FrameUtil.getFrame(resultLocation, frame.getFacing());
        if (fr == null) return;
        fr.setItem(null);
    }

    /*
     *  Ломаем НФТ, идёт проверка соседних рамок на наличие нфт
     */

    public static void breakNFT(ItemFrame frame, Location location, Orient orient) {

        double x, y, z;
        switch (orient.getSide()) {
            case Z_SIDE_INVERTED:
            case Z_SIDE:
                x = location.getX();
                for (z = orient.getZa(); z <= orient.getZb(); z++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        clearFrame(frame, location, x, y, z);
                    }
                }
                break;
            case UP:
            case DOWN:
                y = location.getY();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (z = orient.getZa(); z >= orient.getZb(); z--) {
                        clearFrame(frame, location, x, y, z);

                    }
                }
                break;
            case X_SIDE:
            case X_SIDE_INVERTED:
                z = location.getZ();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        clearFrame(frame, location, x, y, z);
                    }
                }
                break;
        }
    }


}
