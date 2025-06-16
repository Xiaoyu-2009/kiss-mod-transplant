package com.awa.kissmod.packet;

import com.awa.kissmod.KissMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public class KissC2SPacket {
    private final UUID kissedEntityUuid;
    private final UUID senderUuid;

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    public static void encode(KissC2SPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.kissedEntityUuid);
        buffer.writeUUID(packet.senderUuid);
    }

    public static KissC2SPacket decode(FriendlyByteBuf buffer) {
        UUID kissedEntityUuid = buffer.readUUID();
        UUID senderUuid = buffer.readUUID();
        return new KissC2SPacket(kissedEntityUuid, senderUuid);
    }

    public static void handle(KissC2SPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                UUID targetUuid = packet.getKissedEntityUuid();
                UUID senderUuid = packet.getSenderUuid();
                ServerLevel world = player.serverLevel();
                Entity target = world.getEntity(targetUuid);

                if (target != null) {
                    KissS2CPacket broadcastPacket = new KissS2CPacket(target.getUUID(), senderUuid);
                    for (ServerPlayer nearbyPlayer : world.players()) {
                        if (!nearbyPlayer.getUUID().equals(senderUuid)) {
                            KissMod.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> nearbyPlayer), broadcastPacket);
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }
} 