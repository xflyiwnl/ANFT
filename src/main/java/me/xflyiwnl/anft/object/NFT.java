package me.xflyiwnl.anft.object;

import me.xflyiwnl.anft.ANFT;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.anft.object.nft.ImageNFT;
import me.xflyiwnl.anft.object.nft.Point;
import me.xflyiwnl.anft.render.NFTRenderer;
import me.xflyiwnl.anft.util.ImageUtil;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NFT extends NFTObject implements Saveable {

    private int tokenId;
    private String name;
    private String description;

    private UUID owner;

    private ImageNFT image;
    private List<Figure> figures = new ArrayList<Figure>();

    private Point point;
    private boolean isPlaced = false;

    public NFT() {
    }

    public NFT(UUID owner, int w, int h, ImageNFT image) {
        super(w, h);
        this.owner = owner;
        this.image = image;
        image.setImage(ImageUtil.resizeImage(image.getImage(), w, h));
    }

    public NFT(String id, int w, int h, int tokenId, String name, String description, UUID owner, ImageNFT image) {
        super(id, w, h);
        this.tokenId = tokenId;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.image = image;
        image.setImage(ImageUtil.resizeImage(image.getImage(), w, h));
    }

    public NFT(int w, int h, ImageNFT image, List<Figure> figures) {
        super(w, h);
        this.image = image;
        this.figures = figures;
        image.setImage(ImageUtil.resizeImage(image.getImage(), getW(), getH()));
    }

    public void locate() {

    }

    public void frames() {
        int ew = getW() / 128, eh = getH() / 128;
        int w = 128 * ew, h = 128 * eh;
        for (int fw = 0; fw < w / 128; fw++) {
            for (int hw = 0; hw < h / 128; hw++) {
                Figure fig = new Figure(w, h, fw * 128, hw * 128);
                this.getFigures().add(fig);
            }
        }
    }

    @Override
    public void create(boolean save) {
        ANFT.getInstance().getNfts().add(this);
        if (save) {
            save();
        }
    }

    @Override
    public void save() {
        ANFT.getInstance().getFlatFileSource().getNftData().save(this);
    }

    @Override
    public void remove() {
        ANFT.getInstance().getFlatFileSource().getNftData().remove(this);
    }

    public int getTokenId() {
        return tokenId;
    }

    public void setTokenId(int tokenId) {
        this.tokenId = tokenId;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
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
        container.set(ANFT.getInstance().getKey(), PersistentDataType.STRING, getId());

        itemStack.setItemMeta(mapMeta);

        return itemStack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setPlaced(boolean placed) {
        isPlaced = placed;
    }
}
