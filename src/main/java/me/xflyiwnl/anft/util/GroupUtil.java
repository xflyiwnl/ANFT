package me.xflyiwnl.anft.util;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.Group;
import me.xflyiwnl.anft.object.NFT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GroupUtil {

    public static Group getGroup(Player player) {
        Group group = null;
        for (String id : ANFT.getInstance().getGroups().keySet()) {
            Group gr = ANFT.getInstance().getGroup(id);
            if (player.hasPermission("nft.group." + gr.getName())) {
                if (group == null) {
                    group = gr;
                }
                if (group.getId() < gr.getId()) {
                    group = gr;
                }
            }
        }
        if (group == null) {
            Group defaultGroup = ANFT.getInstance().getDefaultGroup();
            if (defaultGroup == null) {
                Bukkit.getLogger().severe("Default group not found, please add it in groups.yml");
            } else {
                group = defaultGroup;
            }
        }
        return group;
    }

}
