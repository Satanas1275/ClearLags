package com.clearlags.client;

import com.clearlags.ClearLags;
import com.clearlags.ClearLagsClient;
import com.clearlags.network.TimerPayload;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ClearLagsOverlay {
    private static final int BOX_SIZE = 36;
    private static final int PADDING = 6;

    public static void register() {
        HudElementRegistry.addLast(
            Identifier.fromNamespaceAndPath(ClearLags.MOD_ID, "overlay"),
            ClearLagsOverlay::extract
        );
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        TimerPayload data = ClearLagsClient.getLastTimerData();
        if (data == null || !data.guiEnabled()) return;

        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        int x, y;
        switch (data.guiPlacement()) {
            case 0 -> { x = PADDING; y = PADDING; }
            case 1 -> { x = w - BOX_SIZE - PADDING; y = PADDING; }
            case 2 -> { x = PADDING; y = h - BOX_SIZE - PADDING; }
            case 3 -> { x = w - BOX_SIZE - PADDING; y = h - BOX_SIZE - PADDING; }
            default -> { x = PADDING; y = PADDING; }
        }

        graphics.fill(x, y, x + BOX_SIZE, y + BOX_SIZE, 0x80000000);
        graphics.outline(x, y, BOX_SIZE, BOX_SIZE, 0xFF00AA00);

        String text = String.valueOf(data.timeLeft());
        int tw = mc.font.width(text);
        graphics.text(mc.font, text,
            x + (BOX_SIZE - tw) / 2,
            y + (BOX_SIZE - mc.font.lineHeight) / 2,
            0xFFFFFFFF, true);
    }
}
