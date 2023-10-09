package me.xflyiwnl.anft.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;

import java.util.List;
import java.util.stream.Collectors;

public class FrameUtil {

    /*
     *  Получаем нфт по локации
     */

    public static List<ItemFrame> getFramesByLocation(Location location) {
        return location.getNearbyEntitiesByType(ItemFrame.class, 0.5).stream().collect(Collectors.toList());
    }

    public static List<ItemFrame> getFramesByLocation(Location location, double radius) {
        return location.getNearbyEntitiesByType(ItemFrame.class, radius).stream().collect(Collectors.toList());
    }

    public static ItemFrame getFrame(List<ItemFrame> frames, BlockFace face) {
        if (frames.isEmpty()) {
            return null;
        }

        for (ItemFrame frame : frames) {
            if (frame.getFacing() == face) {
                return frame;
            }
        }

        return null;
    }

    public static ItemFrame getFrame(Location location, BlockFace face) {
        return getFrame(getFramesByLocation(location), face);
    }

}
