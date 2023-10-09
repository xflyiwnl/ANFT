package me.xflyiwnl.anft.gui;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.ask.Ask;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.*;
import me.xflyiwnl.anft.util.NFTUtil;
import me.xflyiwnl.colorfulgui.builder.inventory.DynamicGuiBuilder;
import me.xflyiwnl.colorfulgui.object.GuiItem;
import me.xflyiwnl.colorfulgui.object.PaginatedGui;
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class SizeGUI extends ColorfulProvider<PaginatedGui> {

    private FileConfiguration yaml;
    private NFT nft;
    private BufferedNFT bufferedNFT;

    public SizeGUI(Player player, NFT nft) {
        super(player);
        this.nft = nft;
        yaml = ANFT.getInstance().getFileManager().getSizeGUI().yaml();
    }

    public SizeGUI(Player player, BufferedNFT nft) {
        super(player);
        this.bufferedNFT = nft;
        yaml = ANFT.getInstance().getFileManager().get("gui/size.yml").yaml();
    }

    @Override
    public void init() {

        if (nft != null && nft.isPlaced()) {
            new MessageSender(getPlayer())
                    .path("nft-placed-error")
                    .run();
            return;
        }

        items();
        nfts();

        show();
    }

    public void nfts() {
        for (Size size : ANFT.getInstance().getSizes()) {

            String path = "size-item.";

            String name = yaml.getString(path + "display-name")
                    .replace("%size%", size.formatted())
                    .replace("%width%", String.valueOf(size.getW()))
                    .replace("%height%", String.valueOf(size.getH()));
            List<String> lore = new ArrayList<String>();
            yaml.getStringList(path + "lore").forEach(s -> {
                lore.add(s.replace("%size%", size.formatted())
                        .replace("%width%", String.valueOf(size.getW()))
                        .replace("%height%", String.valueOf(size.getH())));
            });
            Material material = Material.valueOf(yaml.getString(path + "material").toUpperCase());

            GuiItem nftItem = ANFT.getInstance().getColorfulGUI()
                    .item()
                    .material(material)
                    .name(name)
                    .lore(lore)
                    .amount(1)
                    .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON)
                    .action(event -> {
                        getPlayer().closeInventory();
                        if (bufferedNFT != null) {
                            NFTUtil.giveNFT(getPlayer(), bufferedNFT, size);
                            return;
                        }

                        if (nft != null) {
                            NFTUtil.giveNFT(getPlayer(), nft, size);
                            return;
                        }
                    })
                    .build();
            getGui().addItem(nftItem);
        }
    }

    public void items() {

        if (!yaml.isConfigurationSection("items")) {
            return;
        }

        for (String section : yaml.getConfigurationSection("items").getKeys(false)) {

            String path = "items." + section + ".";

            String name = yaml.getString(path + "display-name");
            List<String> lore = yaml.getStringList(path + "lore");
            int amount = yaml.getInt(path + "amount");
            Material material = Material.valueOf(yaml.getString(path + "material").toUpperCase());
            String mask = yaml.get(path + "mask") == null ? null : yaml.getString(path + "mask");
            List<Integer> slots = yaml.get(path + "slots") == null ? null : yaml.getIntegerList(path + "slots");
            List<String> actions = yaml.get(path + "action") == null ? null : yaml.getStringList(path + "action");

            GuiItem guiItem = ANFT.getInstance().getColorfulGUI()
                    .item()
                    .material(material)
                    .name(name)
                    .lore(lore)
                    .amount(amount)
                    .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON)
                    .action(event -> {
                        if (actions != null) {
                            for (String action : actions) {
                                if (action.equalsIgnoreCase("[next]")) {
                                    getGui().next();
                                } else if (action.equalsIgnoreCase("[previous]")) {
                                    getGui().previous();
                                } else if (action.equalsIgnoreCase("[custom]")) {
                                    getPlayer().closeInventory();
                                    PlayerNFT playerNFT = ANFT.getInstance().getPlayer(getPlayer().getUniqueId());
                                    playerNFT.setAsk(
                                            new Ask(
                                                    getPlayer(),
                                                    Translator.of("ask.messages.size"),
                                                    ask -> {
                                                        String[] splitted = ask.getMessage().getValue().split("x");
                                                        if (splitted.length != 2) {
                                                            new MessageSender(getPlayer())
                                                                    .path("ask.errors.size-error")
                                                                    .run();
                                                            return;
                                                        }
                                                        int w = 0;
                                                        int h = 0;
                                                        try {
                                                            w = Integer.parseInt(splitted[0]);
                                                            h = Integer.parseInt(splitted[1]);
                                                        } catch (NumberFormatException e) {
                                                            new MessageSender(getPlayer())
                                                                    .path("ask.errors.size-error")
                                                                    .run();
                                                        }
                                                        if (w > ANFT.getInstance().getLimitW()
                                                        && h > ANFT.getInstance().getLimitH()) {
                                                            new MessageSender(getPlayer())
                                                                    .path("ask.errors.limit-error")
                                                                    .replace("limit",
                                                                            ANFT.getInstance().getLimitW() + "x" + ANFT.getInstance().getLimitH())
                                                                    .run();
                                                            return;
                                                        }
                                                        Size size = new Size(w, h);

                                                        if (bufferedNFT == null) {
                                                            NFTUtil.giveNFT(getPlayer(), nft, size);
                                                        } else {
                                                            NFTUtil.giveNFT(getPlayer(), bufferedNFT, size);

                                                        }
                                                    },
                                                    () -> {}
                                            )
                                    );
                                }
                            }
                        }
                    })
                    .build();
            if (mask != null) {
                getGui().getMask().addItem(mask, guiItem);
            }
            if (slots != null) {
                slots.forEach(slot -> {
                    getGui().setItem(slot, guiItem);
                });
            }

        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        event.setResult(Event.Result.DENY);
    }

    public static void showGUI(Player player, BufferedNFT nft) {
        FileConfiguration yaml = ANFT.getInstance().getFileManager().getSizeGUI().yaml();
        DynamicGuiBuilder builder = ANFT.getInstance().getColorfulGUI()
                .paginated()
                .holder(new SizeGUI(player, nft))
                .title(
                        yaml.getString("gui.title")
                                .replace("%player%", player.getName())
                )
                .rows(yaml.getInt("gui.rows"));
        if (yaml.get("gui.mask") != null) {
            builder.mask(yaml.getStringList("gui.mask"));
        }
        builder.build();
    }

    public static void showGUI(Player player, NFT nft) {
        FileConfiguration yaml = ANFT.getInstance().getFileManager().getSizeGUI().yaml();
        DynamicGuiBuilder builder = ANFT.getInstance().getColorfulGUI()
                .paginated()
                .holder(new SizeGUI(player, nft))
                .title(
                        yaml.getString("gui.title")
                                .replace("%player%", player.getName())
                )
                .rows(yaml.getInt("gui.rows"));
        if (yaml.get("gui.mask") != null) {
            builder.mask(yaml.getStringList("gui.mask"));
        }
        builder.build();
    }

}
