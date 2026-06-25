package com.clearlags.handler;

import com.clearlags.ClearLags;
import com.clearlags.network.TimerPayload;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.entity.item.ItemEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClearLagsHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger("ClearLags|Handler");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path GUI_PATH = FabricLoader.getInstance().getConfigDir().resolve("clearlags_gui.json");

    private final MinecraftServer server;
    private int tickCounter;
    private int timeLeft;
    private boolean warningSent;
    private final Map<UUID, GuiSettings> guiSettings = new ConcurrentHashMap<>();

    public ClearLagsHandler(MinecraftServer server) {
        this.server = server;
        this.timeLeft = ClearLags.getConfig().clearInterval;
        loadGuiSettings();
    }

    public void tick() {
        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;
        timeLeft--;

        if (timeLeft <= 0) {
            runClear();
            timeLeft = ClearLags.getConfig().clearInterval;
            warningSent = false;
        }

        var config = ClearLags.getConfig();
        if (config.warningMessageEnabled && !warningSent && timeLeft == config.warningTime) {
            warningSent = true;
            String msg = config.warningMessage.replace("%seconds", String.valueOf(timeLeft));
            server.getPlayerList().broadcastSystemMessage(Component.literal(msg), false);
        }

        syncTimer();
    }

    public void runClear() {
        var config = ClearLags.getConfig();
        int total = 0;

        for (ServerLevel world : server.getAllLevels()) {
            var items = world.getEntities(EntityTypeTest.forClass(ItemEntity.class), e -> true);
            for (var item : items) {
                item.discard();
                total++;
            }
        }

        int killed = total;

        if (config.clearMessageEnabled) {
            server.getPlayerList().broadcastSystemMessage(
                Component.literal(config.clearMessage), false
            );
        }

        if (config.afterClearMessageEnabled) {
            String msg = config.afterClearMessage.replace("%killed", String.valueOf(killed));
            server.getPlayerList().broadcastSystemMessage(
                Component.literal(msg), false
            );
        }
    }

    private void syncTimer() {
        for (var player : server.getPlayerList().getPlayers()) {
            if (!ServerPlayNetworking.canSend(player, TimerPayload.TYPE)) continue;
            var gs = guiSettings.getOrDefault(player.getUUID(), GuiSettings.DEFAULT);
            ServerPlayNetworking.send(player, new TimerPayload(timeLeft, gs.enabled(), gs.placement()));
        }
    }

    public void resetTimer() {
        timeLeft = ClearLags.getConfig().clearInterval;
    }

    public int getTimeLeft() { return timeLeft; }

    public void setGuiSettings(UUID uuid, GuiSettings gs) {
        guiSettings.put(uuid, gs);
        saveGuiSettings();
    }

    public GuiSettings getGuiSettings(UUID uuid) {
        return guiSettings.getOrDefault(uuid, GuiSettings.DEFAULT);
    }

    private void loadGuiSettings() {
        if (!Files.exists(GUI_PATH)) return;
        try {
            Type type = new TypeToken<Map<UUID, GuiSettings>>() {}.getType();
            Map<UUID, GuiSettings> loaded = GSON.fromJson(Files.readString(GUI_PATH), type);
            if (loaded != null) guiSettings.putAll(loaded);
        } catch (IOException e) {
            LOGGER.error("Failed to load GUI settings", e);
        }
    }

    private void saveGuiSettings() {
        try {
            Files.createDirectories(GUI_PATH.getParent());
            Files.writeString(GUI_PATH, GSON.toJson(guiSettings));
        } catch (IOException e) {
            LOGGER.error("Failed to save GUI settings", e);
        }
    }
}
