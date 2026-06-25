package com.clearlags.network;

import com.clearlags.ClearLags;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record TimerPayload(int timeLeft, boolean guiEnabled, int guiPlacement) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TimerPayload> TYPE = new CustomPacketPayload.Type<>(
        Identifier.fromNamespaceAndPath(ClearLags.MOD_ID, "timer")
    );

    public static final StreamCodec<FriendlyByteBuf, TimerPayload> CODEC =
        StreamCodec.ofMember(TimerPayload::write, TimerPayload::new);

    public TimerPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean(), buf.readInt());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeInt(timeLeft);
        buf.writeBoolean(guiEnabled);
        buf.writeInt(guiPlacement);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
