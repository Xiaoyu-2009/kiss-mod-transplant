package com.awa.kissmod.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import com.awa.kissmod.client.KissModClient;

import java.util.UUID;
import java.util.function.Supplier;

public class KissS2CPacket {
    private final UUID pattedEntityUuid;
    private final UUID whoPattedUuid;

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid = whoPattedUuid;
    }

    public static void encode(KissS2CPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.pattedEntityUuid);
        buffer.writeUUID(packet.whoPattedUuid);
    }

    public static KissS2CPacket decode(FriendlyByteBuf buffer) {
        UUID pattedEntityUuid = buffer.readUUID();
        UUID whoPattedUuid = buffer.readUUID();
        return new KissS2CPacket(pattedEntityUuid, whoPattedUuid);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(KissS2CPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ClientLevel world = client.level;

            if (world != null) {
                for (Entity entity : world.entitiesForRendering()) {
                    if (entity.getUUID().equals(packet.getPattedEntityUuid())) {
                        if (client.player != null && !client.player.getUUID().equals(packet.getWhoPattedUuid())) {
                            KissModClient.triggerEffect(entity, world);
                            break;
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }
} 