package com.clearlags.config;

import com.clearlags.ClearLags;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClearLagsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("clearlags.json");

    public int clearInterval = 60;
    public int warningTime = 10;
    public String warningMessage = "§e[ClearLags] §fClearing dropped items in §a%seconds §fseconds!";
    public boolean warningMessageEnabled = true;
    public String clearMessage = "§e[ClearLags] §fClearing dropped items...";
    public boolean clearMessageEnabled = true;
    public String afterClearMessage = "§e[ClearLags] §fCleared §a%killed §fitems!";
    public boolean afterClearMessageEnabled = true;

    public static ClearLagsConfig load() {
        if (Files.exists(PATH)) {
            try {
                ClearLagsConfig cfg = GSON.fromJson(Files.readString(PATH), ClearLagsConfig.class);
                if (cfg != null) return cfg;
            } catch (Exception e) {
                ClearLags.LOGGER.error("Failed to load config", e);
            }
        }
        ClearLagsConfig cfg = new ClearLagsConfig();
        cfg.save();
        return cfg;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent());
            Files.writeString(PATH, GSON.toJson(this));
        } catch (IOException e) {
            ClearLags.LOGGER.error("Failed to save config", e);
        }
    }
}
