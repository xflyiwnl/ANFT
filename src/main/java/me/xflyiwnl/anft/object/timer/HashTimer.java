package me.xflyiwnl.anft.object.timer;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.HashedNFT;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HashTimer extends BukkitRunnable {

    public HashTimer() {
        this.runTaskTimerAsynchronously(ANFT.getInstance(), 0, 1200);
    }

    @Override
    public void run() {
        FileConfiguration yaml = ANFT.getInstance().getFileManager().getSettings().yaml();
        List<HashedNFT> nftList = new ArrayList<HashedNFT>();
        ANFT.getInstance().getHashedNFTS().forEach((s, hashedNFT) -> {
            if ((System.currentTimeMillis() - hashedNFT.getCreatedMillis()) / 1000 <
                    yaml.getDouble("settings.nft-hash-update-time")) {
                nftList.add(hashedNFT);
            }
        });

        ANFT.getInstance().getHashedUUIDMap().forEach((s, hashedNFT) -> {
            if ((System.currentTimeMillis() - hashedNFT.getCreatedMillis()) / 1000 <
                    yaml.getDouble("settings.nft-hash-update-time")) {
                nftList.add(hashedNFT);
            }
        });
        nftList.forEach(HashedNFT::remove);

    }

}
