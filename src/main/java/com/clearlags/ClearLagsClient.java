package com.clearlags;

import com.clearlags.client.ClearLagsOverlay;
import com.clearlags.network.TimerPayload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClearLagsClient implements ClientModInitializer {
    private static TimerPayload lastTimerData;

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TimerPayload.TYPE, (payload, context) -> {
            lastTimerData = payload;
        });

        ClearLagsOverlay.register();
    }

    public static TimerPayload getLastTimerData() { return lastTimerData; }
}
