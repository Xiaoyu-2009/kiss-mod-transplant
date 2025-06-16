package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

public class KissModClient {
    public static boolean rightClickEnabled = KissModConfig.loadConfig();
    private static KeyMapping kissKey;
    private static long lastTriggerTime = 0;
    private static final long TRIGGER_INTERVAL = 175;

    public static void init(IEventBus modEventBus) {
        kissKey = registerKeyMapping();

        NeoForge.EVENT_BUS.addListener(KissModClient::onRightClickEntity);

        NeoForge.EVENT_BUS.addListener(KissModClient::onKeyInput);

        modEventBus.addListener(KissModClient::registerKeyBindings);
    }
    
    private static KeyMapping registerKeyMapping() {
        return new KeyMapping(
                "key.kissmod.kiss",
                KeyConflictContext.IN_GAME,
                KeyModifier.NONE,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.kissmod.keybindings"
        );
    }
    
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(kissKey);
    }

    private static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (!rightClickEnabled || !event.getLevel().isClientSide()) return;
        
        if (event.getEntity().isShiftKeyDown()) {
            Entity target = event.getTarget();
            if (target != null) {
                triggerEffect(target, event.getLevel());
                event.setCanceled(true);
            }
        }
    }

    private static void onKeyInput(InputEvent.Key event) {
        if (kissKey != null && kissKey.consumeClick()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTriggerTime >= TRIGGER_INTERVAL) {
                Entity target = Minecraft.getInstance().crosshairPickEntity;
                if (target != null) {
                    if (Minecraft.getInstance().level != null) {
                        triggerEffect(target, Minecraft.getInstance().level);
                    }
                }
                lastTriggerTime = currentTime;
            }
        }
    }

    public static void triggerEffect(Entity target, Level level) {
        if (level.isClientSide()) {
            spawnHeartParticles(level, target);
            SoundEvent[] soundEvents = {
                    KissMod.CUSTOM_SOUND_EVENT,
                    KissMod.CUSTOM_SOUND1_EVENT,
                    KissMod.CUSTOM_SOUND2_EVENT};
            SoundEvent randomSound = soundEvents[new Random().nextInt(soundEvents.length)];

            level.playSound(
                    Minecraft.getInstance().player,
                    target.getX(), target.getY(), target.getZ(),
                    randomSound,
                    SoundSource.PLAYERS,
                    1.0F, 1.0F
            );
        }
    }

    public static void spawnHeartParticles(Level level, Entity entity) {
        if (level.isClientSide()) {
            double x = entity.getX();
            double y = entity.getY() + entity.getBbHeight();
            double z = entity.getZ();

            for (int i = 0; i < 20; i++) {
                double offsetX = level.random.nextDouble() - 0.5;
                double offsetY = level.random.nextDouble() - 0.5;
                double offsetZ = level.random.nextDouble() - 0.5;
                level.addParticle(
                        net.minecraft.core.particles.ParticleTypes.HEART,
                        x + offsetX, y + offsetY, z + offsetZ,
                        0.0, 0.0, 0.0
                );
            }
        }
    }
} 