package me.xflyiwnl.anft.command;

import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.NFT;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NFTCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("Недостаточно аргументов для полноценной команды");
            return true;
        }

        parseNFTCommand(player, args);

        return true;
    }

    public void parseNFTCommand(Player player, String[] args) {
        switch (args[0].toLowerCase()) {
            case "map":
                mapCommand(player, args);
                break;
            default:
                player.sendMessage("Неизвестный аргумент команды");
                break;
        }
    }

    public void mapCommand(Player player, String[] args) {

        if (args.length < 2) {
            player.sendMessage("Недостаточно аргументов для полноценной команды");
            return;
        }

        // size
        String[] ef = args[1].split("x");
        int ew = Integer.parseInt(ef[0]), eh = Integer.parseInt(ef[1]);
        int w = 128 * ew, h = 128 * eh;

        String url = args[2];
        ImageNFT imageNFT = new ImageNFT(url);

        if (!imageNFT.load()) {
            player.sendMessage("Не удалось загрузить изображение");
            return;
        }

        NFT nft = new NFT(w, h, imageNFT);

        for (int fw = 0; fw < w / 128; fw++) {
            for (int hw = 0; hw < h / 128; hw++) {
                Figure fig = new Figure(w, h, fw * 128, hw * 128);
                nft.getFigures().add(fig);
            }
        }

        nft.create(true);

        player.getInventory().addItem(nft.asItemStack(player.getWorld()));
    }

}
