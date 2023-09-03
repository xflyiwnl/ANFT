package me.xflyiwnl.anft;

import me.xflyiwnl.anft.config.YAML;

public class FileManager {

    private YAML settings, language;

    public FileManager() {
    }

    public void generate() {
        settings = new YAML("settings.yml");
        language = new YAML("language.yml");
    }

    public YAML getSettings() {
        return settings;
    }

    public YAML getLanguage() {
        return language;
    }
}
