package com.awa.kissmod.packet;

import com.awa.kissmod.KissMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class KissS2CPacket {

    public static final Identifier PACKET_ID = typeId("kiss_entity_s2c_packet");

    private final UUID pattedEntityUuid;
    private final UUID whoPattedUuid;

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid    = whoPattedUuid;
    }

    public KissS2CPacket(PacketByteBuf buf) {
        this.pattedEntityUuid = buf.readUuid();
        this.whoPattedUuid    = buf.readUuid();
    }

    public PacketByteBuf write() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(this.pattedEntityUuid);
        buf.writeUuid(this.whoPattedUuid);
        return buf;
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }

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
}
