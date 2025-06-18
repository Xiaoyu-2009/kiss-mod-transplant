package com.awa.kissmod.packet;

import com.awa.kissmod.client.KissModClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class KissS2CPacket implements IMessage {
    private UUID pattedEntityUuid;
    private UUID whoPattedUuid;

    public KissS2CPacket() {}

    public KissS2CPacket(UUID pattedEntityUuid, UUID whoPattedUuid) {
        this.pattedEntityUuid = pattedEntityUuid;
        this.whoPattedUuid = whoPattedUuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pattedEntityUuid = new UUID(buf.readLong(), buf.readLong());
        this.whoPattedUuid = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.pattedEntityUuid.getMostSignificantBits());
        buf.writeLong(this.pattedEntityUuid.getLeastSignificantBits());
        buf.writeLong(this.whoPattedUuid.getMostSignificantBits());
        buf.writeLong(this.whoPattedUuid.getLeastSignificantBits());
    }

    public UUID getPattedEntityUuid() {
        return this.pattedEntityUuid;
    }

    public UUID getWhoPattedUuid() {
        return this.whoPattedUuid;
    }

    public static class Handler implements IMessageHandler<KissS2CPacket, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(KissS2CPacket message, MessageContext ctx) {
            Minecraft minecraft = Minecraft.getMinecraft();
            WorldClient world = minecraft.world;

            if (world != null) {
                minecraft.addScheduledTask(() -> {
                    for (Entity entity : world.loadedEntityList) {
                        if (entity.getUniqueID().equals(message.getPattedEntityUuid())) {
                            if (minecraft.player != null && !minecraft.player.getUniqueID().equals(message.getWhoPattedUuid())) {
                                KissModClient.triggerEffect(entity, world);
                                break;
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
} 