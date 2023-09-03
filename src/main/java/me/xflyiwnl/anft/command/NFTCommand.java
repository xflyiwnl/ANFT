package me.xflyiwnl.anft.command;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.Figure;
import me.xflyiwnl.anft.object.ImageNFT;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.renderer.NFTRenderer;
import me.xflyiwnl.anft.util.ImageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
            System.out.println("STOP");
            for (int hw = 0; hw < h / 128; hw++) {
                System.out.println("STOP");
                Figure fig = new Figure(w, h, fw * 128, hw * 128);
                nft.getFigures().add(fig);
                System.out.println(fw * 128 + " / " + hw * 128);
            }
        }

        ANFT.getInstance().getNfts().add(nft);

        player.getInventory().addItem(nft.asItemStack(player.getWorld()));

//        BufferedImage image = load(args[2]);
//
//        if (image == null) {
//            player.sendMessage("Не удалось загрузить изображение");
//            return;
//        }
//

    }

//
//    public BufferedImage load(String url) {
//        BufferedImage image = null;
//        try {
//            image = ImageIO.read(new URL(url));
//        } catch (IOException e) {
//            return image;
//        }
//        return image;
//    }
//

//    public void createImage(Player player, NFTRenderer renderer, int w, int h, int cw, int ch) {
//
//        renderer.change(ImageUtil.resizeImage(renderer.get(), w, h));
//        renderer.crop(cw, ch);
//
//        MapView view = Bukkit.createMap(player.getWorld());
//        view.getRenderers().clear();
//        view.addRenderer(renderer);
//
//        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
//        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
//        mapMeta.setMapView(view);
//        itemStack.setItemMeta(mapMeta);
//
//        player.getInventory().addItem(itemStack);
//        player.sendMessage("Выдано!");
//    }
}
