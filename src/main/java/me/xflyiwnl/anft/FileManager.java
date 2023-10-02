package me.xflyiwnl.anft;

import me.xflyiwnl.anft.config.YAML;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {

    private YAML settings, language, size, errors, groups;
    private File nftsFolder, playersFolder, imageFolder;
    private YAML nftGUI, sizeGUI;

    public FileManager() {
    }

    public void generate() {
        mkdir();
        settings = new YAML("settings.yml");
        language = new YAML("language.yml");
        size = new YAML("size.yml");
        errors = new YAML("errors.yml");
        nftGUI = new YAML("gui/nft-list.yml");
        sizeGUI = new YAML("gui/size.yml");
        groups = new YAML("groups.yml");
    }

    public YAML get(String path) {
        return new YAML(path);
    }

    public void mkdir() {
        nftsFolder = new File(ANFT.getInstance().getDataFolder(), "nfts");
        if (!nftsFolder.exists()) {
            nftsFolder.mkdir();
        }
        playersFolder = new File(ANFT.getInstance().getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }
        imageFolder = new File(ANFT.getInstance().getDataFolder(), "nfts/images");
        if (!imageFolder.exists()) {
            imageFolder.mkdir();
        }
    }

    public YAML getSettings() {
        return settings;
    }

    public YAML getLanguage() {
        return language;
    }

    public File getNftsFolder() {
        return nftsFolder;
    }

    public File getPlayersFolder() {
        return playersFolder;
    }

    public YAML getSize() {
        return size;
    }

    public YAML getErrors() {
        return errors;
    }

    public YAML getNftGUI() {
        return nftGUI;
    }

    public YAML getSizeGUI() {
        return sizeGUI;
    }

    public YAML getGroups() {
        return groups;
    }

    public File getImageFolder() {
        return imageFolder;
    }
}
