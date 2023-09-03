package me.xflyiwnl.anft.config;

import me.xflyiwnl.anft.ANFT;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YAML {

    private File file;
    private FileConfiguration yaml;

    public YAML(String name) {
        init(name);
    }

    public void init(String name) {

        file = new File(ANFT.getInstance().getDataFolder(), name);
        if (!file.exists()) {
            ANFT.getInstance().saveResource(name, true);
        }
        yaml = YamlConfiguration.loadConfiguration(file);

    }

    public void save() {
        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File file() {
        return file;
    }

    public FileConfiguration yaml() {
        return yaml;
    }

}
