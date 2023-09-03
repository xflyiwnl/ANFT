package me.xflyiwnl.anft.listener;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        if (playerNFT == null) {
            playerNFT = new PlayerNFT(player.getUniqueId());
            playerNFT.create();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        if (playerNFT != null) {
            playerNFT.getRendered().clear();
        }
    }

}
