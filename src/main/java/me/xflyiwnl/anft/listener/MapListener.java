package me.xflyiwnl.anft.listener;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.Figure;
import me.xflyiwnl.anft.object.ImageNFT;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.Point;
import me.xflyiwnl.anft.renderer.NFTRenderer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MapListener implements Listener {

    @EventHandler
    public void onBreakNFT(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        if (!(event.getEntity() instanceof ItemFrame)) {
            return;
        }

        Player player = (Player) event.getDamager();
        ItemFrame frame = (ItemFrame) event.getEntity();

        ItemStack itemStack = frame.getItem();

        MapMeta itemMeta = (MapMeta) itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(ANFT.getInstance().getKey(), PersistentDataType.STRING)) {
            return;
        }
        UUID uniqueId = UUID.fromString(container.get(ANFT.getInstance().getKey(), PersistentDataType.STRING));
        NFT nft = ANFT.getInstance().getNFT(uniqueId);
        if (nft == null) {
            return;
        }

        Location location = nft.getPoint().getLocation();
        int z = location.getBlockZ();
        for (double x = location.getBlockX() + 0.5; x <= location.getBlockX() + (nft.getW() / 128); x++) {
            for (double y = location.getBlockY() + (nft.getH() / 128) - 0.5; y >= location.getBlockY(); y--) {
                Location resultLocation = new Location(location.getWorld(), x, y, z);
                List<ItemFrame> frames = resultLocation.getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().collect(Collectors.toList());

                if (frames.isEmpty()) {
                    continue;
                }

                ItemFrame fr = frames.get(0);
                fr.setItem(null);
            }
        }

    }

    @EventHandler
    public void onPlaceNFT(PlayerInteractEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        NFT nft = null;

        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        ItemStack mapItem = player.getEquipment().getItemInMainHand();
        if (mapItem == null) {
            return;
        }

        if (entity != null && !(entity instanceof ItemFrame)) {
            return;
        }

        MapMeta itemMeta = (MapMeta) mapItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(ANFT.getInstance().getKey(), PersistentDataType.STRING)) {
            return;
        }

        UUID uniqueId = UUID.fromString(container.get(ANFT.getInstance().getKey(), PersistentDataType.STRING));
        nft = ANFT.getInstance().getNFT(uniqueId);
        if (nft == null) {
            return;
        }

        if (!checkFrames(entity.getLocation(), nft)) {
            player.sendMessage("Недостаточно рамок");
            return;
        }

        ItemFrame frame = (ItemFrame) entity;

        player.sendMessage(event.getRightClicked().getLocation().toString());
        setFrames(player.getWorld(), entity.getLocation(), nft);

        nft.setPoint(new Point(frame.getLocation(), frame.getFacing()));

        event.setCancelled(true);

    }

    public boolean checkFrames(Location location, NFT nft) {
        double z = location.getBlockZ();
        for (double x = location.getBlockX() + 0.5; x <= location.getBlockX() + (nft.getW() / 128); x++) {
            System.out.println("STOP");
            for (double y = location.getBlockY() + (nft.getH() / 128) - 0.5; y >= location.getBlockY(); y--) {
                System.out.println("STOP");

                Location resultLocation = new Location(location.getWorld(), x, y, z);
                List<ItemFrame> frames = resultLocation.getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().collect(Collectors.toList());

                if (frames.isEmpty()) {
                    return false;
                }

            }
        }
        return true;
    }

    public boolean setFrames(World world, Location location, NFT nft) {

        int fw = 0;
        int fh = 0;

        int z = location.getBlockZ();
        for (double x = location.getBlockX() + 0.5; x <= location.getBlockX() + (nft.getW() / 128); x++) {
            System.out.println("STOP");
            for (double y = location.getBlockY() + (nft.getH() / 128) - 0.5; y >= location.getBlockY(); y--) {
                System.out.println("STOP");

                Location resultLocation = new Location(location.getWorld(), x, y, z);
                List<ItemFrame> frames = resultLocation.getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().collect(Collectors.toList());

                if (frames.isEmpty()) {
                    return false;
                }

                ItemFrame frame = frames.get(0);

                System.out.println(fw + " / " + fh);

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

                if (nft.getH() / 128 - 1 == fh) {
                    fh = 0;
                } else {
                    fh++;
                }
            }
            fw++;
        }
        return true;
    }

}
