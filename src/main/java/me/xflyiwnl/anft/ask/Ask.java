package me.xflyiwnl.anft.ask;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.PlayerNFT;
import org.bukkit.entity.Player;

public class Ask {

    private Player player;
    private PlayerNFT playerNFT;
    private String message;
    private AskAction<AskMessage> onChat;
    private Runnable onCancel;

    public Ask(Player player, String message, AskAction<AskMessage> onChat, Runnable onCancel) {
        this.player = player;
        this.message = message;
        this.onChat = onChat;
        this.onCancel = onCancel;
        playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        init();
    }

    public void init() {
        if (playerNFT.hasAsk()) {
            new MessageSender(player)
                    .path("ask.has-ask")
                    .run();
            return;
        }

        playerNFT.setAsk(this);
        new MessageSender(player)
                .path("ask.message")
                .replace("message", message)
                .run();
    }

    public void onChat(AskMessage ask) {
        if (ask.getMessage().getValue().equalsIgnoreCase("отмена")) {
            cancel();
            return;
        }

        onChat.execute(ask);
        remove();
    }

    public void cancel() {
        new MessageSender(player)
                .path("ask.cancel")
                .run();
        onCancel.run();
        remove();
    }

    public void remove() {
        playerNFT.setAsk(null);
    }

}
