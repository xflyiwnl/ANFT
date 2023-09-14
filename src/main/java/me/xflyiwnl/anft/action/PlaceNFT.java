package me.xflyiwnl.anft.action;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.util.NFTUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class PlaceNFT implements Action {

    private Player player;
    private Entity entity;

    public PlaceNFT(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    public ItemStack mapItem() {
        return player.getEquipment().getItemInMainHand();
    }

    public NFT getNFTfromItem() {
        NFT nft = null;

        ItemStack mapItem = mapItem();
        if (mapItem == null) {
            return null;
        }

        if (entity != null && !(entity instanceof ItemFrame)) {
            return null;
        }

        MapMeta itemMeta = (MapMeta) mapItem.getItemMeta();
        if (itemMeta == null) {
            return null;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(ANFT.getInstance().getKey(), PersistentDataType.STRING)) {
            return null;
        }

        UUID uniqueId = UUID.fromString(container.get(ANFT.getInstance().getKey(), PersistentDataType.STRING));
        nft = ANFT.getInstance().getNFT(uniqueId);
        if (nft == null) {
            return null;
        }
        return nft;
    }

    public Orient orient(NFT nft, Location location, OrientSide side) {
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

    public OrientSide side(ItemFrame frame) {
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

    @Override
    public boolean execute() {

        NFT nft = getNFTfromItem();

        if (nft == null) {
            return false;
        }

        if (nft.isPlaced()) {
            player.sendMessage("Этот нфт уже поставлен");
            return true;
        }

        ItemFrame frame = (ItemFrame) entity;
        Location location = entity.getLocation();

        OrientSide side = side(frame);
        Orient orient = orient(nft, location, side);

        if (!NFTUtil.checkFrames(entity.getLocation(), nft, frame.getFacing(), orient)) {
            player.sendMessage("Недостаточно рамок");
            return true;
        }

        NFTUtil.populateFrames(player.getWorld(), entity.getLocation(), nft, frame.getFacing(), orient);
        nft.setPoint(new Point(frame.getLocation(), frame.getFacing()));
        nft.setPlaced(true);
        nft.save();

        player.sendMessage("place");

        return true;
    }

}
