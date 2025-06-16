package com.awa.kissmod;

import com.awa.kissmod.client.KissModClient;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(KissMod.MOD_ID)
public class KissMod {
    public static final String MOD_ID = "kissmod";
    
    public static final ResourceLocation CUSTOM_SOUND_ID = ResourceLocation.parse(MOD_ID + ":custom_sound");
    public static final SoundEvent CUSTOM_SOUND_EVENT = SoundEvent.createVariableRangeEvent(CUSTOM_SOUND_ID);
    
    public static final ResourceLocation CUSTOM_SOUND1_ID = ResourceLocation.parse(MOD_ID + ":custom_sound1");
    public static final SoundEvent CUSTOM_SOUND1_EVENT = SoundEvent.createVariableRangeEvent(CUSTOM_SOUND1_ID);
    
    public static final ResourceLocation CUSTOM_SOUND2_ID = ResourceLocation.parse(MOD_ID + ":custom_sound2");
    public static final SoundEvent CUSTOM_SOUND2_EVENT = SoundEvent.createVariableRangeEvent(CUSTOM_SOUND2_ID);

    public KissMod(IEventBus modEventBus) {
        modEventBus.addListener(this::registerSounds);
        
        if (FMLEnvironment.dist == Dist.CLIENT) {
            KissModClient.init(modEventBus);
        }
    }
    
    private void registerSounds(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (event.getRegistryKey().equals(BuiltInRegistries.SOUND_EVENT.key())) {
            Registry.register(BuiltInRegistries.SOUND_EVENT, CUSTOM_SOUND_ID, CUSTOM_SOUND_EVENT);
            Registry.register(BuiltInRegistries.SOUND_EVENT, CUSTOM_SOUND1_ID, CUSTOM_SOUND1_EVENT);
            Registry.register(BuiltInRegistries.SOUND_EVENT, CUSTOM_SOUND2_ID, CUSTOM_SOUND2_EVENT);
        }
    }
}