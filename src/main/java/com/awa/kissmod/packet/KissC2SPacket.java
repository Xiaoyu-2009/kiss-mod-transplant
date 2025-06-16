package com.awa.kissmod.packet;

import com.awa.kissmod.KissMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissC2SPacket {
    public static final Identifier PACKET_ID = typeId("kiss_entity_c2s_packet");

    private final UUID kissedEntityUuid;
    private final UUID senderUuid;

    public static Identifier typeId(String id) {
        String namespace = KissMod.MOD_ID;
        String path = id;
        String[] split = path.split(":");
        if (split.length >= 2) {
            namespace = split[0];
            path      = split[1];
        }
        return new Identifier(namespace, path);
    }

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    public KissC2SPacket(PacketByteBuf buf) {
        this.kissedEntityUuid = buf.readUuid();
        this.senderUuid = buf.readUuid();
    }

    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.kissedEntityUuid);
        buf.writeUuid(this.senderUuid);
        return buf;
    }
}

