package me.xflyiwnl.anft.command;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.action.TotalClean;
import me.xflyiwnl.anft.chat.MessageSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CleanCommand implements CommandExecutor, TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            return Arrays.asList(
                    "rma"
            );
        }

        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!sender.hasPermission("nft.rma")) {
            new MessageSender(player)
                    .path("no-permission")
                    .run();
            return true;
        }

        if (ANFT.getInstance().getNfts().isEmpty()) {
            new MessageSender(player)
                    .path("nfts-empty")
                    .run();
            return true;
        }

        new TotalClean().execute();

        return true;
    }

}
