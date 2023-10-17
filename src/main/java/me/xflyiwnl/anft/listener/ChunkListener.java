package me.xflyiwnl.anft.listener;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.render.NFTRenderer;
import me.xflyiwnl.anft.util.ImageUtil;
import me.xflyiwnl.anft.util.NFTUtil;
import me.xflyiwnl.anft.util.OrientUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;

public class ChunkListener implements Listener {

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {

        Chunk chunk = event.getChunk();

        if (ANFT.getInstance().getChunks().contains(chunk)) {
            event.setSaveChunk(true);
        }

    }

    @EventHandler
    public void onImageLoad(ChunkLoadEvent event) {

        for (Entity entity : event.getChunk().getEntities()) {

            if (!(entity instanceof ItemFrame)) continue;
            ItemFrame frame = (ItemFrame) entity;
            ItemStack itemStack = frame.getItem();

            if (itemStack.getType() != Material.FILLED_MAP) continue;

            NFT nft = NFTUtil.getNFTfromMap(itemStack);
            if (nft == null) {
                continue;
            }

            if (nft.isLoaded()) {
                continue;
            }

            Point point = nft.getPoint();
            if (point == null) continue;

            Location location = point.getLocation();
            Orient orient = OrientUtil.orient(nft, location, OrientUtil.side(frame));

            new BukkitRunnable() {

                @Override
                public void run() {
                    NFTUtil.loadFrames(event.getWorld(), location, nft, frame.getFacing(), orient);
                }
            }.runTaskAsynchronously(ANFT.getInstance());

            nft.setLoaded(true);

        }

    }

}
