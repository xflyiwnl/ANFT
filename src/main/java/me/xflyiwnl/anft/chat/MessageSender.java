package me.xflyiwnl.anft.chat;

import me.xflyiwnl.anft.object.Translator;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MessageSender {

    private Player player;
    private String path;
    private Message message;

    private HashMap<String, String> replace = new HashMap<String, String>();

    public MessageSender(Player player) {
        this.player = player;
    }

    public MessageSender() {
    }

    public MessageSender path(String path) {
        this.path = path;
        return this;
    }

    public MessageSender player(Player player) {
        this.player = player;
        return this;
    }

    public MessageSender replace(String placeholder, String value) {
        replace.put(placeholder, value);
        return this;
    }

    public MessageSender message(String text) {
        message = new Message(text);
        return this;
    }

    public Message run() {
        if (message == null) {
            message = new Message(Translator.of(path));
        }
        replace.forEach((s, s2) -> {
            String replacedText = message.getValue().replace("%" + s + "%", s2);
            message.setValue(replacedText);
        });
        player.sendMessage(message.getValue());
        return message;
    }

}
