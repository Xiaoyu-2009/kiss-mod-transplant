package com.awa.kissmod.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class KissS2CPacket {
    private final UUID pattedEntityUuid;
    private final UUID whoPattedUuid;

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid = whoPattedUuid;
    }

    public static void encode(KissS2CPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.pattedEntityUuid);
        buf.writeUUID(packet.whoPattedUuid);
    }

    public static KissS2CPacket decode(FriendlyByteBuf buf) {
        return new KissS2CPacket(buf.readUUID(), buf.readUUID());
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }
}