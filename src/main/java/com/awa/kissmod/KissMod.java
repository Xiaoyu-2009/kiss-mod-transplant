package com.awa.kissmod;

import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class KissMod implements ModInitializer {
	public static final String MOD_ID = "kiss-mod";
	public static final Identifier CUSTOM_SOUND_ID = new Identifier(MOD_ID, "custom_sound");
	public static final SoundEvent CUSTOM_SOUND_EVENT = Registry.register(
			Registries.SOUND_EVENT,
			CUSTOM_SOUND_ID,
			SoundEvent.of(CUSTOM_SOUND_ID)
	);
	public static final Identifier CUSTOM_SOUND1_ID = new Identifier(MOD_ID, "custom_sound1");
	public static final SoundEvent CUSTOM_SOUND1_EVENT = Registry.register(
			Registries.SOUND_EVENT,
			CUSTOM_SOUND1_ID,
			SoundEvent.of(CUSTOM_SOUND1_ID)
	);

	public static final Identifier CUSTOM_SOUND2_ID = new Identifier(MOD_ID, "custom_sound2");
	public static final SoundEvent CUSTOM_SOUND2_EVENT = Registry.register(
			Registries.SOUND_EVENT,
			CUSTOM_SOUND2_ID,
			SoundEvent.of(CUSTOM_SOUND2_ID)
	);

	@Override
	public void onInitialize() {
		registerNetworkReceiver();
	}
	private void registerNetworkReceiver() {
		ServerPlayNetworking.registerGlobalReceiver(KissC2SPacket.PACKET_ID, (server, player, handler, buf, responseSender) -> {
			KissC2SPacket packet = new KissC2SPacket(buf);
			UUID targetUuid = packet.getKissedEntityUuid();
			UUID senderUuid = packet.getSenderUuid();
			World world = player.getWorld();
			Entity target = ((ServerWorld) world).getEntity(targetUuid);

			if (target != null) {
				KissS2CPacket broadcastPacket = new KissS2CPacket(target.getUuid(), senderUuid);
				for (ServerPlayerEntity nearbyPlayer : ((ServerWorld) world).getPlayers()) {
					if (!nearbyPlayer.getUuid().equals(senderUuid)) {
						ServerPlayNetworking.send(nearbyPlayer, KissS2CPacket.PACKET_ID, broadcastPacket.write());
					}
				}
			}
		});
	}
}