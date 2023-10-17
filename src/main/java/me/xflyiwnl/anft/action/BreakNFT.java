package me.xflyiwnl.anft.action;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.util.NFTUtil;
import me.xflyiwnl.anft.util.OrientUtil;
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
     *  Тут происходит весь движ
     *  проверка всего и ломание нфт
     */

    @Override
    public boolean execute() {

        ItemStack itemStack = frame.getItem();

        NFT nft = NFTUtil.getNFTfromMap(itemStack);
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
        Orient orient = OrientUtil.orient(nft, location, OrientUtil.side(frame));
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
