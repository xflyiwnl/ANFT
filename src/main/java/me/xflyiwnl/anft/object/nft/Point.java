package me.xflyiwnl.anft.object.nft;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class Point {

    private Location location;
    private BlockFace face;

    public Point(Location location, BlockFace face) {
        this.location = location;
        this.face = face;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BlockFace getFace() {
        return face;
    }

    public void setFace(BlockFace face) {
        this.face = face;
    }
}
