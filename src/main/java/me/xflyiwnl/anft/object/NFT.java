package me.xflyiwnl.anft.object;

import io.papermc.paper.text.PaperComponents;
import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.renderer.NFTRenderer;
import me.xflyiwnl.anft.util.ImageUtil;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.awt.print.Paper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NFT extends NFTObject {

    private ImageNFT image;
    private List<Figure> figures = new ArrayList<Figure>();

    private Point point;

    public NFT() {
    }

    public NFT(int w, int h, ImageNFT image) {
        super(w, h);
        this.image = image;
        image.setImage(ImageUtil.resizeImage(image.getImage(), w, h));
    }

    public NFT(int w, int h, ImageNFT image, List<Figure> figures) {
        super(w, h);
        this.image = image;
        this.figures = figures;
        image.setImage(ImageUtil.resizeImage(image.getImage(), getW(), getH()));
    }

    public Figure getFigure(int fw, int fh) {
        for (Figure figure : figures) {
            if (figure.getOw() == fw && figure.getOh() == fh) {
                return figure;
            }
        }
        return null;
    }

    public ItemStack asItemStack(World world) {

        NFTRenderer renderer = new NFTRenderer(image);

        MapView view = Bukkit.createMap(world);
        view.getRenderers().clear();
        view.addRenderer(renderer);

        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapView(view);

        PersistentDataContainer container = mapMeta.getPersistentDataContainer();
        container.set(ANFT.getInstance().getKey(), PersistentDataType.STRING, getUniqueId().toString());

        itemStack.setItemMeta(mapMeta);

        return itemStack;
    }

    public ImageNFT getImage() {
        return image;
    }

    public void setImage(ImageNFT image) {
        this.image = image;
    }

    public List<Figure> getFigures() {
        return figures;
    }

    public void setFigures(List<Figure> figures) {
        this.figures = figures;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
