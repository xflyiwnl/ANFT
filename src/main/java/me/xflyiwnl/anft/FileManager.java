package me.xflyiwnl.anft;

import me.xflyiwnl.anft.config.YAML;

import java.io.File;

public class FileManager {

    private YAML settings, language;
    private File playersFolder, nftsFolder;

    public FileManager() {
    }

    public void generate() {
        mkdir();
        settings = new YAML("settings.yml");
        language = new YAML("language.yml");
    }

    public void mkdir() {
        playersFolder = new File(ANFT.getInstance().getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdir();
        }
        nftsFolder = new File(ANFT.getInstance().getDataFolder(), "nfts");
        if (!nftsFolder.exists()) {
            nftsFolder.mkdir();
        }
    }

    public YAML getSettings() {
        return settings;
    }

    public YAML getLanguage() {
        return language;
    }

    public File getPlayersFolder() {
        return playersFolder;
    }

    public File getNftsFolder() {
        return nftsFolder;
    }

}
