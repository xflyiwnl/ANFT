package me.xflyiwnl.anft.listener;

import me.xflyiwnl.anft.action.BreakNFT;
import me.xflyiwnl.anft.action.PlaceNFT;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class MapListener implements Listener {

    @EventHandler
    public void onBreakNFT(EntityDamageByEntityEvent event) {

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

}
