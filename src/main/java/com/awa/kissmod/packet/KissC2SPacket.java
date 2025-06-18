package com.awa.kissmod.packet;

import com.awa.kissmod.KissMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class KissC2SPacket implements IMessage {
    private UUID kissedEntityUuid;
    private UUID senderUuid;

    public KissC2SPacket() {}

    public KissC2SPacket(UUID kissedEntityUuid, UUID senderUuid) {
        this.kissedEntityUuid = kissedEntityUuid;
        this.senderUuid = senderUuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.kissedEntityUuid = new UUID(buf.readLong(), buf.readLong());
        this.senderUuid = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.kissedEntityUuid.getMostSignificantBits());
        buf.writeLong(this.kissedEntityUuid.getLeastSignificantBits());
        buf.writeLong(this.senderUuid.getMostSignificantBits());
        buf.writeLong(this.senderUuid.getLeastSignificantBits());
    }

    public UUID getKissedEntityUuid() {
        return this.kissedEntityUuid;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public static class Handler implements IMessageHandler<KissC2SPacket, IMessage> {
        @Override
        public IMessage onMessage(KissC2SPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (player != null) {
                WorldServer world = player.getServerWorld();
                UUID targetUuid = message.getKissedEntityUuid();
                UUID senderUuid = message.getSenderUuid();
                
                player.getServerWorld().addScheduledTask(() -> {
                    Entity target = null;
                    for (Entity entity : world.loadedEntityList) {
                        if (entity.getUniqueID().equals(targetUuid)) {
                            target = entity;
                            break;
                        }
                    }

                    if (target != null) {
                        KissS2CPacket broadcastPacket = new KissS2CPacket(target.getUniqueID(), senderUuid);
                        for (EntityPlayerMP nearbyPlayer : world.getMinecraftServer().getPlayerList().getPlayers()) {
                            if (!nearbyPlayer.getUniqueID().equals(senderUuid)) {
                                KissMod.NETWORK_CHANNEL.sendTo(broadcastPacket, nearbyPlayer);
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
}