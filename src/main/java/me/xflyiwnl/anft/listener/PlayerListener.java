package me.xflyiwnl.anft.listener;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.ask.AskMessage;
import me.xflyiwnl.anft.chat.Message;
import me.xflyiwnl.anft.object.HashedNFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        if (playerNFT == null) {
            playerNFT = new PlayerNFT(player.getUniqueId());
            playerNFT.create(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        HashedNFT hashedNFT = ANFT.getInstance().getHashedNFT(player.getUniqueId());
        if (playerNFT != null) {
            playerNFT.getRendered().clear();
        }
        if (hashedNFT != null) {
            hashedNFT.remove();
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {

        Player player = event.getPlayer();
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());

        if (playerNFT == null) return;
        if (!playerNFT.hasAsk()) return;

        playerNFT.getAsk().onChat(new AskMessage(new Message(event.getMessage())));
        event.setCancelled(true);

    }

}
