package me.xflyiwnl.anft.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.action.BreakNFT;
import me.xflyiwnl.anft.action.PlaceNFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.render.NFTRenderer;
import me.xflyiwnl.anft.util.FrameUtil;
import me.xflyiwnl.anft.util.NFTUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MapListener implements Listener {

    @EventHandler
    public void onBreakNFT(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof ItemFrame)) {
            return;
        }

        BreakNFT breakNFT = new BreakNFT(
                (Player) event.getDamager(),
                (ItemFrame) event.getEntity()
        );
        if (breakNFT.execute()) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlaceNFT(PlayerInteractEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        PlaceNFT create = new PlaceNFT(
                event.getPlayer(),
                event.getRightClicked()
        );
        if (create.execute()) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onRender(MapInitializeEvent event) {

        MapView view = event.getMap();

        Figure figure = ANFT.getInstance().getFigure(view.getId());
        if (figure == null) {
            return;
        }

        NFT nft = figure.getNft();
        if (nft == null) {
            return;
        }

        if (!nft.getImage().isImageLoaded()) {
            return;
        }

        ImageNFT image = nft.getImage();

        NFTRenderer renderer = new NFTRenderer(image.clone().crop(figure.getOw(), figure.getOh()));

        view.getRenderers().clear();
        view.addRenderer(renderer);

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Location location = event.getBlock().getLocation().clone().subtract(-0.5, -0.5, -0.5);
        List<ItemFrame> frames = FrameUtil.getFramesByLocation(location, 0.6);

        if (frames.isEmpty()) return;

        boolean contains = NFTUtil.containsNFT(frames);

        if (contains) {
            new MessageSender(event.getPlayer())
                    .path("break-nft-block")
                    .run();
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Location location = event.getBlock().getLocation().clone().subtract(-0.5, -0.5, -0.5);
        List<ItemFrame> frames = FrameUtil.getFramesByLocation(location, 0.5);

        if (frames.isEmpty()) return;

        boolean contains = NFTUtil.containsNFT(frames);

        if (contains) {
            new MessageSender(event.getPlayer())
                    .path("placed-block-on-nft")
                    .run();
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplodeEntity(HangingBreakByEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        Entity entity = event.getEntity();

        if (!(entity instanceof ItemFrame)) {
            return;
        }

        ItemFrame frame = (ItemFrame) entity;
        if (!NFTUtil.isNFT(frame)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplodeBlock(EntityExplodeEvent event) {

        if (event.isCancelled()) {
            return;
        }

        List<Block> remove = new ArrayList<Block>();
        for (Block block : event.blockList()) {
            Location location = block.getLocation().clone().subtract(-0.5, -0.5, -0.5);
            List<ItemFrame> frames = FrameUtil.getFramesByLocation(location, 0.6);

            if (frames.isEmpty()) continue;

            boolean contains = NFTUtil.containsNFT(frames);

            if (contains) {
                remove.add(block);
            }
        }

        event.blockList().removeAll(remove);

    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (event.getBlocks().isEmpty()) {

            Location location = event.getBlock().getLocation().clone().subtract(-0.5, -0.5, -0.5);

            double x = 0, y = 0, z = 0;

            if (event.getDirection() == BlockFace.NORTH) {
                z = 1 * event.getLength() + 1;
            } else if (event.getDirection() == BlockFace.SOUTH) {
                z = -(1 * event.getLength() + 1);
            }

            if (event.getDirection() == BlockFace.EAST) {
                x = -(1 * event.getLength() + 1);
            } else if (event.getDirection() == BlockFace.WEST) {
                x = 1 * event.getLength() + 1;
            }

            if (event.getDirection() == BlockFace.UP) {
                y = -(1 * event.getLength() + 1);
            } else if (event.getDirection() == BlockFace.DOWN) {
                y = 1 * event.getLength() + 1;
            }

            location = location.clone().subtract(x, y, z);

            List<ItemFrame> frames = FrameUtil.getFramesByLocation(location, 0.5);

            if (frames.isEmpty()) return;

            boolean contains = NFTUtil.containsNFT(frames);

            if (contains) {
                event.setCancelled(true);
            }
        } else {
            for (Block block: event.getBlocks()) {
                Location location = block.getLocation().clone().subtract(-0.5, -0.5, -0.5);
                List<ItemFrame> frames = FrameUtil.getFramesByLocation(location, 0.6);

                if (frames.isEmpty()) continue;

                boolean contains = NFTUtil.containsNFT(frames);

                if (contains) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

    }

}
