package me.xflyiwnl.anft.util;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.*;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.renderer.NFTRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class NFTUtil {

    public static boolean checkFrame(Location location, BlockFace face, double x, double y, double z) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        ItemFrame frame = FrameUtil.getFrame(resultLocation, face);

        if (frame == null) {
            return true;
        }
        return false;
    }

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

    public static void populateFrame(World world, Location location, NFT nft, BlockFace face, Orient orient, double x, double y, double z, int fw, int fh) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        ItemFrame frame = FrameUtil.getFrame(resultLocation, face);

        Figure figure = nft.getFigure(fw * 128, fh * 128);
        ImageNFT image = nft.getImage();

        NFTRenderer renderer = new NFTRenderer(image.clone().crop(fw * 128, fh * 128));

        MapView view = Bukkit.createMap(world);
        view.getRenderers().clear();
        view.addRenderer(renderer);

        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapView(view);
        PersistentDataContainer container = mapMeta.getPersistentDataContainer();
        container.set(ANFT.getInstance().getKey(), PersistentDataType.STRING, nft.getUniqueId().toString());
        itemStack.setItemMeta(mapMeta);

        frame.setItem(itemStack);
    }

    public static void populateFrames(World world, Location location, NFT nft, BlockFace face, Orient orient) {
        int fw = 0, fh = 0;
        double x, y, z;
        switch (orient.getSide()) {
            case Z_SIDE:
            case Z_SIDE_INVERTED:
                fw = orient.getSide() == OrientSide.Z_SIDE_INVERTED ? (nft.getW() / 128 - 1) : 0;
                x = location.getX();
                for (z = orient.getZa(); z <= orient.getZb(); z++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        populateFrame(world, location, nft, face, orient, x, y, z, fw, fh);
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
                        populateFrame(world, location, nft, face, orient, x, y, z, fw, fh);

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
                        populateFrame(world, location, nft, face, orient, x, y, z, fw, fh);
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

    public static void clearFrame(ItemFrame frame, Location location, double x, double y, double z) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        ItemFrame fr = FrameUtil.getFrame(resultLocation, frame.getFacing());
        fr.setItem(null);
    }

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
