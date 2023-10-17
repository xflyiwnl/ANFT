package me.xflyiwnl.anft.command;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.Group;
import me.xflyiwnl.anft.object.PlayerNFT;
import me.xflyiwnl.anft.object.Translator;
import me.xflyiwnl.colorfulgui.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("nft.group")) {
            sender.sendMessage(TextUtil.colorize(Translator.of("no-permission")));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(TextUtil.colorize(Translator.of("not-enough-args")));
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

        if (player == null) {
            sender.sendMessage(TextUtil.colorize(Translator.of("unknown-player")));
            return true;
        }

        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());

        if (playerNFT == null) {
            sender.sendMessage(TextUtil.colorize(Translator.of("unknown-player")));
            return true;
        }

        Group group = ANFT.getInstance().getGroup(args[2]);

        if (group == null) {
            sender.sendMessage(TextUtil.colorize(Translator.of("unknown-group")));
            return true;
        }

        sender.sendMessage(TextUtil.colorize(Translator.of("group-changed")));
        playerNFT.setGroup(group);
        playerNFT.save();

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("set");
        } else if (args.length == 2) {
            List<String> players = new ArrayList<String>();
            Bukkit.getOnlinePlayers().forEach(
                    player -> {
                        players.add(player.getName());
                    }
            );
            return players;
        } else if (args.length == 3) {
            List<String> groups = new ArrayList<String>();
            ANFT.getInstance().getGroups().forEach(
                    (s, group) -> {
                        groups.add(group.getName());
                    }
            );
            return groups;
        }
        return null;
    }

}
