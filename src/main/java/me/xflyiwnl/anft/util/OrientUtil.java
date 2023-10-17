package me.xflyiwnl.anft.util;

import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

public class OrientUtil {

    public static Orient orient(NFT nft, Location location, OrientSide side) {
        Orient orient = new Orient();
        orient.setSide(side);

        switch (side) {
            case UP:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setZa(location.getZ() + (nft.getH() / 128) - 1);
                orient.setZb(location.getZ());
                break;
            case DOWN:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setZb(location.getZ() - (nft.getH() / 128) + 1);
                orient.setZa(location.getZ());
                break;
            case Z_SIDE:
                orient.setZa(location.getZ());
                orient.setZb(location.getZ() + (nft.getW() / 128) - 1);

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            case Z_SIDE_INVERTED:
                orient.setZa(location.getZ() - (nft.getW() / 128) + 1);
                orient.setZb(location.getZ());

                orient.setYa(location.getY() + (    nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            case X_SIDE_INVERTED:
                orient.setXa(location.getX() - (nft.getW() / 128) + 1);
                orient.setXb(location.getX());

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            case X_SIDE:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            default:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
        }

        return orient;
    }

    public static OrientSide side(ItemFrame frame) {
        switch (frame.getFacing()) {
            case SOUTH:
                return OrientSide.X_SIDE;
            case NORTH:
                return OrientSide.X_SIDE_INVERTED;
            case EAST:
                return OrientSide.Z_SIDE_INVERTED;
            case WEST:
                return OrientSide.Z_SIDE;
            case UP:
                return OrientSide.UP;
            case DOWN:
                return OrientSide.DOWN;
            default:
                return OrientSide.X_SIDE;
        }
    }

}
