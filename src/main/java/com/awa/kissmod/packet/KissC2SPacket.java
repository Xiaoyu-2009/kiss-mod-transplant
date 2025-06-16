package com.awa.kissmod.packet;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class KissC2SPacket {
    private final UUID kissedEntityUuid;
    private final UUID senderUuid;

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    public static void encode(KissC2SPacket packet, FriendlyByteBuf buf) {
        buf.writeUUID(packet.kissedEntityUuid);
        buf.writeUUID(packet.senderUuid);
    }

    public static KissC2SPacket decode(FriendlyByteBuf buf) {
        return new KissC2SPacket(buf.readUUID(), buf.readUUID());
    }

    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }
}