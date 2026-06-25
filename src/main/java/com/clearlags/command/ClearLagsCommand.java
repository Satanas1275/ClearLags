package com.clearlags.command;

import com.clearlags.ClearLags;
import com.clearlags.config.ClearLagsConfig;
import com.clearlags.handler.GuiSettings;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

import static net.minecraft.commands.Commands.literal;

public class ClearLagsCommand {
    private static final String[] PLACEMENT_NAMES = {"Top-Left", "Top-Right", "Bottom-Left", "Bottom-Right"};

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("clearlags")
            .then(literal("clear")
                .requires(src -> src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                .executes(ctx -> {
                    var handler = ClearLags.getHandler();
                    if (handler == null) {
                        ctx.getSource().sendFailure(Component.literal("§c[ClearLags] Server not ready"));
                        return 0;
                    }
                    handler.runClear();
                    handler.resetTimer();
                    ctx.getSource().sendSuccess(() ->
                        Component.literal("§e[ClearLags] §fManual clear triggered!"), true);
                    return 1;
                })
            )
            .then(literal("reload")
                .requires(src -> src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.ADMINS)))
                .executes(ctx -> {
                    ClearLags.setConfig(ClearLagsConfig.load());
                    var handler = ClearLags.getHandler();
                    if (handler != null) handler.resetTimer();
                    ctx.getSource().sendSuccess(() ->
                        Component.literal("§e[ClearLags] §fConfig reloaded!"), true);
                    return 1;
                })
            )
            .then(literal("gui")
                .requires(src -> {
                    var p = src.getPlayer();
                    return p != null && ServerPlayNetworking.canSend(p, com.clearlags.network.TimerPayload.TYPE);
                })
                .then(literal("on")
                    .executes(ctx -> {
                        var player = ctx.getSource().getPlayer();
                        if (player == null) {
                            ctx.getSource().sendFailure(Component.literal("§c[ClearLags] Players only"));
                            return 0;
                        }
                        var uuid = player.getUUID();
                        var gs = ClearLags.getHandler().getGuiSettings(uuid);
                        ClearLags.getHandler().setGuiSettings(uuid, new GuiSettings(true, gs.placement()));
                        ctx.getSource().sendSuccess(() ->
                            Component.literal("§e[ClearLags] §fGUI enabled"), true);
                        return 1;
                    })
                )
                .then(literal("off")
                    .executes(ctx -> {
                        var player = ctx.getSource().getPlayer();
                        if (player == null) {
                            ctx.getSource().sendFailure(Component.literal("§c[ClearLags] Players only"));
                            return 0;
                        }
                        var uuid = player.getUUID();
                        var gs = ClearLags.getHandler().getGuiSettings(uuid);
                        ClearLags.getHandler().setGuiSettings(uuid, new GuiSettings(false, gs.placement()));
                        ctx.getSource().sendSuccess(() ->
                            Component.literal("§e[ClearLags] §fGUI disabled"), true);
                        return 1;
                    })
                )
                .then(literal("placement")
                    .executes(ctx -> {
                        var player = ctx.getSource().getPlayer();
                        if (player == null) {
                            ctx.getSource().sendFailure(Component.literal("§c[ClearLags] Players only"));
                            return 0;
                        }
                        var uuid = player.getUUID();
                        var gs = ClearLags.getHandler().getGuiSettings(uuid);
                        int next = (gs.placement() + 1) % 4;
                        ClearLags.getHandler().setGuiSettings(uuid, new GuiSettings(gs.enabled(), next));
                        ctx.getSource().sendSuccess(() ->
                            Component.literal("§e[ClearLags] §fGUI moved to " + PLACEMENT_NAMES[next]), true);
                        return 1;
                    })
                )
            )
        );
    }
}
