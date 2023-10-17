package me.xflyiwnl.anft.render;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class NFTRenderer extends MapRenderer {

    private BufferedImage image;

    public NFTRenderer(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        if (!playerNFT.getRendered().contains(map.getId())) {
            canvas.drawImage(0, 0, MapPalette.resizeImage(image));
            map.setTrackingPosition(false);
            playerNFT.getRendered().add(map.getId());
        }
    }

    public BufferedImage getImage() {
        return image;
    }
}
