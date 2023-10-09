package me.xflyiwnl.anft.action;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.util.NFTUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BreakNFT implements Action {

    private Player player;
    private ItemFrame frame;

    public BreakNFT(Player player, ItemFrame frame) {
        this.player = player;
        this.frame = frame;
    }

    /*
     *  Получаем нфт из айтемстака
     */

    public NFT getNFTfromMap(ItemStack itemStack) {
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
     *  Получаем ориентацию и устанавливаем размеры рамок
     *  откуда и до какой точки
     */

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

    /*
     *  Получаем сторону ориентации
     */

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

    /*
     *  Тут происходит весь движ
     *  проверка всего и ломание нфт
     */

    @Override
    public boolean execute() {

        ItemStack itemStack = frame.getItem();

        NFT nft = getNFTfromMap(itemStack);
        if (nft == null) {
            return false;
        }

        if (!nft.isPlaced()) {
            return false;
        }

        if (!player.isOp() && !player.getUniqueId().equals(nft.getOwner())) {
            new MessageSender(player)
                    .path("break-other-nft")
                    .run();
            return true;
        }

        Point point = nft.getPoint();
        if(point == null) {
            return false;
        }

        Location location = point.getLocation();
        Orient orient = orient(nft, location, side(frame));
        NFTUtil.breakNFT(frame, location, orient);
        nft.setPlaced(false);
        nft.resetFigures();
        nft.save();

        if (player.getUniqueId().equals(nft.getOwner())) {
            player.getInventory().addItem(nft.asItemStack(player.getWorld()));
        }

        new MessageSender(player)
                .path("break-nft")
                .run();

        return true;
    }

}
