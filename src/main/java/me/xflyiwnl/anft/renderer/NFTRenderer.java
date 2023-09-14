package me.xflyiwnl.anft.renderer;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.PlayerNFT;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class NFTRenderer extends MapRenderer {

    private ImageNFT image;

    public NFTRenderer(ImageNFT image) {
        this.image = image;
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
        if (!playerNFT.getRendered().contains(map.getId())) {
            canvas.drawImage(0, 0, MapPalette.resizeImage(image.getImage()));
            map.setTrackingPosition(false);
            playerNFT.getRendered().add(map.getId());
        }
    }

    public void change(ImageNFT image) {
        this.image = image;
    }

    public ImageNFT get() {
        return image;
    }

}
