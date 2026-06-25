package com.clearlags;

import com.clearlags.command.ClearLagsCommand;
import com.clearlags.config.ClearLagsConfig;
import com.clearlags.handler.ClearLagsHandler;
import com.clearlags.network.TimerPayload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClearLags implements ModInitializer {
    public static final String MOD_ID = "clearlags";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ClearLagsConfig config;
    private static ClearLagsHandler handler;

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(TimerPayload.TYPE, TimerPayload.CODEC);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            ClearLagsCommand.register(dispatcher)
        );

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            config = ClearLagsConfig.load();
            handler = new ClearLagsHandler(server);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (handler != null) handler.tick();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            handler = null;
        });

        LOGGER.info("ClearLags initialized");
    }

    public static ClearLagsConfig getConfig() { return config; }
    public static void setConfig(ClearLagsConfig c) { config = c; }
    public static ClearLagsHandler getHandler() { return handler; }
}
