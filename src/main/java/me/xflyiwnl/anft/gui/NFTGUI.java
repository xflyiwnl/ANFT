package me.xflyiwnl.anft.gui;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.*;
import me.xflyiwnl.anft.util.GroupUtil;
import me.xflyiwnl.colorfulgui.builder.inventory.DynamicGuiBuilder;
import me.xflyiwnl.colorfulgui.object.GuiItem;
import me.xflyiwnl.colorfulgui.object.PaginatedGui;
import me.xflyiwnl.colorfulgui.provider.ColorfulProvider;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class NFTGUI extends ColorfulProvider<PaginatedGui> {

    private List<NFT> nfts;
    private List<BufferedNFT> buffered;
    private FileConfiguration yaml;

    public NFTGUI(Player player, List<NFT> nfts, List<BufferedNFT> buffered) {
        super(player);
        this.nfts = nfts;
        this.buffered = buffered;
        yaml = ANFT.getInstance().getFileManager().getNftGUI().yaml();
    }

    @Override
    public void init() {
        items();
        nfts();
        show();
    }

    public void nfts() {

        String yes = Translator.of("yes");
        String no = Translator.of("no");

        for (NFT nft : nfts) {

            String path = "nft-item.loaded.";
            String name = yaml.getString(path + "display-name")
                    .replace("%name%", nft.getName())
                    .replace("%placed%", nft.isPlaced() ? yes : no)
                    .replace("%token%", String.valueOf(nft.getTokenId()));
            List<String> lore = new ArrayList<String>();
            yaml.getStringList(path + "lore").forEach(s -> {
                if (s.contains("%description%")) {
                    String line = "";
                    String[] splitted = nft.getDescription().replace("\n", "").split(" ");
                    for (int i = 0; i < splitted.length; i++) {

                        if (line.length() > 50) {
                            lore.add(yaml.getString(path + "description-format")
                                    .replace("%description%", line));
                            line = "";
                            continue;
                        }

                        if (i + 1 == splitted.length) {
                            line = line + splitted[i] + " ";
                            lore.add(yaml.getString(path + "description-format")
                                    .replace("%description%", line));
                        } else {
                            line = line + splitted[i] + " ";
                        }

                    }
                } else {
                    lore.add(s.replace("%name%", nft.getName())
                            .replace("%placed%", nft.isPlaced() ? yes : no)
                            .replace("%token%", String.valueOf(nft.getTokenId())));
                }
            });
            Material material = Material.valueOf(yaml.getString(path + "material").toUpperCase());

            GuiItem barrierItem = ANFT.getInstance().getColorfulGUI()
                    .item()
                    .material(material)
                    .name(name)
                    .lore(lore)
                    .amount(1)
                    .enchant(Enchantment.PROTECTION_FIRE, 1)
                    .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON)
                    .action(event -> {
                        SizeGUI.showGUI(getPlayer(), nft);
                    })
                    .build();
            getGui().addItem(barrierItem);
        }

        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(getPlayer().getUniqueId());

        Group group = GroupUtil.getGroup(getPlayer());
        for (BufferedNFT nft : buffered) {

            boolean limit;
            String path;
            if (group.getLimit() != -1 && nfts.size() >= group.getLimit()) {
                path = "nft-item.limit.";
                limit = true;
            } else {
                path = "nft-item.unloaded.";
                limit = false;
            }
            String name = yaml.getString(path + "display-name")
                    .replace("%name%", nft.getName())
                    .replace("%token%", String.valueOf(nft.getTokenId()))
                    .replace("%limit%", String.valueOf(group.getLimit()));
            List<String> lore = new ArrayList<String>();
            yaml.getStringList(path + "lore").forEach(s -> {
                if (s.contains("%description%")) {
                    String line = "";
                    String[] splitted = nft.getDescription().replace("\n", "").split(" ");
                    for (int i = 0; i < splitted.length; i++) {

                        if (line.length() > 50) {
                            lore.add(yaml.getString(path + "description-format")
                                    .replace("%description%", line));
                            line = "";
                            continue;
                        }

                        if (i + 1 == splitted.length) {
                            line = line + splitted[i] + " ";
                            lore.add(yaml.getString(path + "description-format")
                                    .replace("%description%", line));
                        } else {
                            line = line + splitted[i] + " ";
                        }

                    }
                } else {
                    lore.add(s.replace("%name%", nft.getName())
                            .replace("%token%", String.valueOf(nft.getTokenId()))
                            .replace("%limit%", String.valueOf(group.getLimit())));
                }
            });
            Material material = Material.valueOf(yaml.getString(path + "material").toUpperCase());

            GuiItem barrierItem = ANFT.getInstance().getColorfulGUI()
                    .item()
                    .material(material)
                    .name(name)
                    .lore(lore)
                    .amount(1)
                    .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON)
                    .action(event -> {
                        if (!limit) {
                            SizeGUI.showGUI(getPlayer(), nft);
                        }
                    })
                    .build();
            getGui().addItem(barrierItem);
        }
    }

    public void items() {

        if (!yaml.isConfigurationSection("items")) {
            return;
        }

        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(getPlayer().getUniqueId());

        Group group = GroupUtil.getGroup(getPlayer());
        for (String section : yaml.getConfigurationSection("items").getKeys(false)) {

            String path = "items." + section + ".";

            String name = yaml.getString(path + "display-name")
                    .replace("%limit%", String.valueOf(group.getLimit()))
                    .replace("%status%", group.getName());
            List<String> lore = new ArrayList<String>();
            yaml.getStringList(path + "lore").forEach(s ->
                    lore.add(s
                            .replace("%limit%", String.valueOf(group.getLimit()))
                    .replace("%status%", group.getName())));
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

    public static void showGUI(Player player, List<NFT> nfts, List<BufferedNFT> buffered) {
        FileConfiguration yaml = ANFT.getInstance().getFileManager().getNftGUI().yaml();
        DynamicGuiBuilder builder = ANFT.getInstance().getColorfulGUI()
                .paginated()
                .holder(new NFTGUI(player, nfts, buffered))
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
