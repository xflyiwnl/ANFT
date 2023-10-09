package me.xflyiwnl.anft.action;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.chat.MessageSender;
import me.xflyiwnl.anft.object.NFT;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.object.orient.Orient;
import me.xflyiwnl.anft.object.orient.OrientSide;
import me.xflyiwnl.anft.util.FrameUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TotalClean implements Action {

    public Orient orient(NFT nft, Location location, OrientSide side) {
        Orient orient = new Orient();
        orient.setSide(side);

        switch (side) {
            case UP:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setZa(location.getZ() + (nft.getH() / 128) - 1);
                orient.setZb(location.getZ());
                break;
            case DOWN:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setZb(location.getZ() - (nft.getH() / 128) + 1);
                orient.setZa(location.getZ());
                break;
            case Z_SIDE:
                orient.setZa(location.getZ());
                orient.setZb(location.getZ() + (nft.getW() / 128) - 1);

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            case Z_SIDE_INVERTED:
                orient.setZa(location.getZ() - (nft.getW() / 128) + 1);
                orient.setZb(location.getZ());

                orient.setYa(location.getY() + (    nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            case X_SIDE_INVERTED:
                orient.setXa(location.getX() - (nft.getW() / 128) + 1);
                orient.setXb(location.getX());

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            case X_SIDE:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
            default:
                orient.setXa(location.getX());
                orient.setXb(location.getX() + (nft.getW() / 128) - 1);

                orient.setYa(location.getY() + (nft.getH() / 128) - 1);
                orient.setYb(location.getY());
                break;
        }

        return orient;
    }

    public OrientSide side(ItemFrame frame) {
        switch (frame.getFacing()) {
            case SOUTH:
                return OrientSide.X_SIDE;
            case NORTH:
                return OrientSide.X_SIDE_INVERTED;
            case EAST:
                return OrientSide.Z_SIDE_INVERTED;
            case WEST:
                return OrientSide.Z_SIDE;
            case UP:
                return OrientSide.UP;
            case DOWN:
                return OrientSide.DOWN;
            default:
                return OrientSide.X_SIDE;
        }
    }

    public void broadcast(MessageSender messageSender) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            messageSender.player(player);
            messageSender.run();
        }
    }

    @Override
    public boolean execute() {

        List<NFT> nfts = ANFT.getInstance().getNfts();
        Queue<NFT> queuedNfts = new LinkedList<NFT>(nfts);

        broadcast(new MessageSender().path("nft-rma-started"));

        new BukkitRunnable() {

            @Override
            public void run() {

                if (queuedNfts.isEmpty()) {
                    broadcast(new MessageSender().path("nft-rma-end"));
                    cancel();
                }

                NFT nft = queuedNfts.poll();

                if (nft == null) {
                    return;
                }

                if (!nft.isPlaced()) {
                    broadcast(new MessageSender().path("nft-rma-nft-not-placed"));
                    nft.remove();
                    return;
                }

                Point point = nft.getPoint();
                if (point == null) return;

                Location location = point.getLocation();
                BlockFace face = point.getFace();

                Chunk chunk = location.getChunk();
                if (!chunk.isLoaded()) {
                    ANFT.getInstance().getChunks().add(chunk);
                    chunk.load();
                }

                ItemFrame frame = FrameUtil.getFrame(location, face);
                if (frame == null) return;

                Orient orient = orient(nft, location, side(frame));

                long time = System.currentTimeMillis();
                int frames = breakNFT(frame, location, orient);
                time = System.currentTimeMillis() - time;
                broadcast(new MessageSender()
                        .path("nft-rma-frame-count")
                        .replace("frames", String.valueOf(frames))
                        .replace("time", String.valueOf(time))
                        .replace("world", location.getWorld().getName()));

                nft.setPlaced(false);
                nft.setPoint(null);

                nft.remove();

                ANFT.getInstance().getHashedNFTS().clear();

            }

        }.runTaskTimer(ANFT.getInstance(), 0, 20);

        return true;
    }

    public static void clearFrame(ItemFrame frame, Location location, double x, double y, double z) {
        Location resultLocation = new Location(location.getWorld(), x, y, z);
        Chunk chunk = resultLocation.getChunk();
        if (!chunk.isLoaded()) {
            ANFT.getInstance().getChunks().add(chunk);
            chunk.load();
        }

        ItemFrame fr = FrameUtil.getFrame(resultLocation, frame.getFacing());
        if (fr == null) return;
        fr.setItem(null);
    }

    public static int breakNFT(ItemFrame frame, Location location, Orient orient) {
        int amount = 0;
        double x, y, z;
        switch (orient.getSide()) {
            case Z_SIDE_INVERTED:
            case Z_SIDE:
                x = location.getX();
                for (z = orient.getZa(); z <= orient.getZb(); z++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        clearFrame(frame, location, x, y, z);
                        amount++;
                    }
                }
                break;
            case UP:
            case DOWN:
                y = location.getY();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (z = orient.getZa(); z >= orient.getZb(); z--) {
                        clearFrame(frame, location, x, y, z);
                        amount++;
                    }
                }
                break;
            case X_SIDE:
            case X_SIDE_INVERTED:
                z = location.getZ();
                for (x = orient.getXa(); x <= orient.getXb(); x++) {
                    for (y = orient.getYa(); y >= orient.getYb(); y--) {
                        clearFrame(frame, location, x, y, z);
                        amount++;
                    }
                }
                break;
        }
        ANFT.getInstance().getChunks().clear();
        return amount;
    }

}
