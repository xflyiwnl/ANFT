package me.xflyiwnl.anft;

import me.xflyiwnl.anft.command.*;
import me.xflyiwnl.anft.database.FlatFileSource;
import me.xflyiwnl.anft.listener.ChunkListener;
import me.xflyiwnl.anft.listener.MapListener;
import me.xflyiwnl.anft.listener.PlayerListener;
import me.xflyiwnl.anft.object.*;
import me.xflyiwnl.anft.object.Error;
import me.xflyiwnl.anft.object.nft.Figure;
import me.xflyiwnl.colorfulgui.ColorfulGUI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ANFT extends JavaPlugin {

    private static ANFT instance;

    private FileManager fileManager = new FileManager();
    private FlatFileSource flatFileSource = new FlatFileSource();
    private NamespacedKey key = new NamespacedKey(this, "anft");
    private ColorfulGUI colorfulGUI;

    private List<Group> groups = new ArrayList<Group>();
    private List<NFT> nfts = new ArrayList<NFT>();
    private List<PlayerNFT> players = new ArrayList<PlayerNFT>();
    private List<Size> sizes = new ArrayList<Size>();
    private List<Error> errors = new ArrayList<Error>();
    private List<HashedNFT> hashedNFTS = new ArrayList<HashedNFT>();
    private List<Chunk> chunks = new ArrayList<Chunk>();

    private int limitW = 10, limitH = 10;
    private String webserver;
    private Group defaultGroup;

    @Override
    public void onEnable() {
        instance = this;

        colorfulGUI = new ColorfulGUI(this);

        fileManager.generate();
        loadErrors();
        loadSizes();
        loadServer();
        loadGroups();

        flatFileSource.load();

        registerCommand();
        registerListener();

    }

    @Override
    public void onDisable() {
        flatFileSource.unload();
    }

    /*
    *   Регистрация команд
    */

    public void registerCommand() {
        getCommand("nft").setExecutor(new NFTCommand());
        getCommand("anft").setExecutor(new ANFTCommand());

        getCommand("verify").setExecutor(new VerifyCommand());
        getCommand("verify").setTabCompleter(new VerifyCommand());

        getCommand("nftgroup").setExecutor(new GroupCommand());
        getCommand("nftgroup").setTabCompleter(new GroupCommand());

        getCommand("nftclean").setExecutor(new CleanCommand());
        getCommand("nftclean").setTabCompleter(new CleanCommand());
    }

    /*
     *  Регистрация слушателей
     */

    public void registerListener () {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new MapListener(), this);
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new ChunkListener(), this);
    }

    /*
     *  Регистрация ошибок (ответы от сервера)
     */

    public void loadErrors() {
        FileConfiguration yaml = fileManager.getErrors().yaml();
        if (!yaml.isConfigurationSection("errors")) {
            return;
        }
        for (String key : yaml.getConfigurationSection("errors").getKeys(false)) {

            String path = "errors." + key + ".";

            int code = yaml.getInt(path + "id");
            String description = yaml.getString(path + "description");

            Error error = new Error(code, description);
            errors.add(error);

        }
    }

    /*
     *  Загрузка эндпоинта (вебсервер и порт)
     */

    public void loadServer() {
        webserver = fileManager.getSettings().yaml().getString("settings.web-server");
    }

    /*
     *  Получение ошибки из памяти (Циклом проходим по всем существующим ошибкам и сравниваем айдишники)
     */

    public Error getError(int code) {
        for (Error error : errors) {
            if (error.getCode() == code) {
                return error;
            }
        }
        return new Error(code);
    }

    /*
     *  Загрузка размеров изображений
     */

    public void loadSizes() {
        FileConfiguration yaml = fileManager.getSize().yaml();
        for (String formattedSize : yaml.getStringList("size.default-sizes")) {
            String[] splittedSize = formattedSize.split("x");
            sizes.add(new Size(Integer.valueOf(splittedSize[0]), Integer.valueOf(splittedSize[1])));
        }
        limitW = yaml.getInt("size.limit.w");
        limitH = yaml.getInt("size.limit.h");
    }

    /*
     *  Загрузка групп (Премиум, дефолт)
     */

    public void loadGroups() {
        FileConfiguration yaml = fileManager.getGroups().yaml();
        if (!yaml.isConfigurationSection("groups")) {
            return;
        }
        for (String key : yaml.getConfigurationSection("groups").getKeys(false)) {
            String path = "groups." + key + ".";
            String name = yaml.getString(path + "name");
            int limit = yaml.getInt(path + "limit");
            boolean isdefault = yaml.getBoolean(path + "default");
            Group group = new Group(
                    name, limit, isdefault
            );
            groups.add(group);
            if (group.isDefault()) {
                defaultGroup = group;
            }
        }
        if (defaultGroup == null) {
            Bukkit.getLogger().severe("Стандартной группы (groups.yml / default: true) не существует. Прошу вас создать стандартную группу так, как без него плагин будет работать неккоректно");
        }
    }

    /*
     *  Проверяем всех игроков после перезагрузки на наличие их в памяти и сохранены ли они вообще
     */

    public void checkPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerNFT playerNFT = ANFT.getInstance().getPlayer(player.getUniqueId());
            if (playerNFT == null) {
                playerNFT = new PlayerNFT(player.getUniqueId());
                playerNFT.create(true);
            }
        }
    }

    /*
     *  Получение НФТ из памяти, проходим циклом по всем НФТ и сравниваем айди
     */

    public NFT getNFT(String id) {
        for (NFT nft : nfts) {
            if (nft.getId().equals(id)) {
                return nft;
            }
        }
        return null;
    }

    /*
     *  Получаем часть изображения НФТ
     */

    public Figure getFigure(int mapId) {
        for (NFT nft : nfts) {
            if (!nft.isPlaced()) {
                continue;
            }
            for (Figure figure : nft.getFigures()) {
                if (figure.getMapId() == mapId) {
                    return figure;
                }
            }
        }
        return null;
    }

    /*
     *  Получаем игрока из памяти (они хранят их статус верификации, их нфтшки и кулдауны)
     */

    public PlayerNFT getPlayer(UUID uniqueId) {
        for (PlayerNFT player : players) {
            if (player.getUniqueId().equals(uniqueId)) {
                return player;
            }
        }
        return null;
    }

    /*
     *  Получаем группу из памяти (Премиум, дефолт и другие)
     */

    public Group getGroup(String name) {
        for (Group group : groups) {
            if (group.getName().equalsIgnoreCase(name)) {
                return group;
            }
        }
        return defaultGroup;
    }

    /*
     *  Получаем хэшированный список НФТ после запроса
     *  Он хранит нфт игрока
     */

    public HashedNFT getHashedNFT(UUID uniqueId) {
        for (HashedNFT hashedNFT : hashedNFTS) {
            if (hashedNFT.getUniqueId() != null && hashedNFT.getUniqueId().equals(uniqueId)) {
                return hashedNFT;
            }
        }
        return null;
    }

    /*
     *  Получаем хэшированный список НФТ после запроса
     *  Он хранит нфт адреса
     */

    public HashedNFT getHashedNFT(String address) {
        for (HashedNFT hashedNFT : hashedNFTS) {
            if (hashedNFT.getAddress() != null && hashedNFT.getAddress().equalsIgnoreCase(address)) {
                return hashedNFT;
            }
        }
        return null;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public List<PlayerNFT> getPlayers() {
        return players;
    }

    public List<NFT> getNfts() {
        return nfts;
    }

    public FlatFileSource getFlatFileSource() {
        return flatFileSource;
    }

    public static ANFT getInstance() {
        return instance;
    }

    public ColorfulGUI getColorfulGUI() {
        return colorfulGUI;
    }

    public List<Size> getSizes() {
        return sizes;
    }

    public int getLimitW() {
        return limitW;
    }

    public int getLimitH() {
        return limitH;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public String getWebserver() {
        return webserver;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Group getDefaultGroup() {
        return defaultGroup;
    }

    public List<HashedNFT> getHashedNFTS() {
        return hashedNFTS;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }
}
