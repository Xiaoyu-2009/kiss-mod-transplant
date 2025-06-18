package com.awa.kissmod.client;

import com.awa.kissmod.KissMod;
import com.awa.kissmod.KissModConfig;
import com.awa.kissmod.packet.KissC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.Random;
import java.util.UUID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class KissModClient {
    public static boolean rightClickEnabled = KissModConfig.loadConfig();
    private static KeyBinding kissKey;
    private static boolean wasKeyPressed = false;
    private static long lastTriggerTime = 0;
    private static final long TRIGGER_INTERVAL = 175;

    public static void init() {
        kissKey = new KeyBinding(
        "key.kissmod.kiss", KeyConflictContext.IN_GAME,
        KeyModifier.NONE, Keyboard.KEY_F7,
        "category.kissmod.keybindings");
        ClientRegistry.registerKeyBinding(kissKey);
        MinecraftForge.EVENT_BUS.register(KissModClient.class);
    }

    @SubscribeEvent
    public static void onRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (!rightClickEnabled || !event.getWorld().isRemote)
            return;

        if (event.getEntityPlayer().isSneaking()) {
            Entity target = event.getTarget();
            if (target != null) {
                UUID senderUuid = null;
                if (Minecraft.getMinecraft().player != null) {
                    senderUuid = Minecraft.getMinecraft().player.getUniqueID();
                }
                KissC2SPacket packet = new KissC2SPacket(target.getUniqueID(), senderUuid);
                KissMod.NETWORK_CHANNEL.sendToServer(packet);
                triggerEffect(target, event.getWorld());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        boolean isKeyPressed = kissKey.isKeyDown();
        long currentTime = System.currentTimeMillis();

        if (isKeyPressed && (!wasKeyPressed || (currentTime - lastTriggerTime >= TRIGGER_INTERVAL))) {
            Minecraft client = Minecraft.getMinecraft();
            Entity target = client.objectMouseOver != null && client.objectMouseOver.entityHit != null
                    ? client.objectMouseOver.entityHit
                    : null;
            if (target != null && client.player != null) {
                UUID senderUuid = client.player.getUniqueID();
                KissC2SPacket packet = new KissC2SPacket(target.getUniqueID(), senderUuid);
                KissMod.NETWORK_CHANNEL.sendToServer(packet);
                if (client.world != null) {
                    triggerEffect(target, client.world);
                }
            }
            lastTriggerTime = currentTime;
        }
        wasKeyPressed = isKeyPressed;
    }

    public static void triggerEffect(Entity target, World world) {
        if (world.isRemote) {
            spawnHeartParticles(world, target);

            Random random = new Random();
            int soundIndex = random.nextInt(3);
            net.minecraft.util.SoundEvent soundEvent = KissMod.CUSTOM_SOUNDS[soundIndex];

            world.playSound(
                    Minecraft.getMinecraft().player.posX,
                    Minecraft.getMinecraft().player.posY,
                    Minecraft.getMinecraft().player.posZ,
                    soundEvent,
                    SoundCategory.PLAYERS,
                    1.0F, 1.0F, false);
        }
    }

    public static void spawnHeartParticles(World world, Entity entity) {
        if (world.isRemote) {
            double x = entity.posX;
            double y = entity.posY + entity.height;
            double z = entity.posZ;

            for (int i = 0; i < 20; i++) {
                double offsetX = world.rand.nextDouble() - 0.5;
                double offsetY = world.rand.nextDouble() - 0.5;
                double offsetZ = world.rand.nextDouble() - 0.5;
                world.spawnParticle(
                        EnumParticleTypes.HEART,
                        x + offsetX, y + offsetY, z + offsetZ,
                        0.0, 0.0, 0.0);
            }
        }
    }

    public static void toggleRightClickEnabled() {
        rightClickEnabled = !rightClickEnabled;
        KissModConfig.saveConfig(rightClickEnabled);
    }

    public static void setRightClickEnabled(boolean state) {
        rightClickEnabled = state;
        KissModConfig.saveConfig(state);
    }
}