package com.awa.kissmod;

import com.awa.kissmod.client.KissModClient;
import com.awa.kissmod.packet.KissC2SPacket;
import com.awa.kissmod.packet.KissS2CPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

@Mod(modid = KissMod.MOD_ID, name = "Kiss Mod", version = "1.0.0", acceptedMinecraftVersions = "[1.12.2]")
@Mod.EventBusSubscriber
public class KissMod {
    public static final String MOD_ID = "kissmod";

    @Mod.Instance(MOD_ID)
    public static KissMod instance;

    public static final SimpleNetworkWrapper NETWORK_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static SoundEvent[] CUSTOM_SOUNDS = new SoundEvent[3];
    
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ResourceLocation customSoundLoc = new ResourceLocation(MOD_ID, "custom_sound");
        ResourceLocation customSound1Loc = new ResourceLocation(MOD_ID, "custom_sound1");
        ResourceLocation customSound2Loc = new ResourceLocation(MOD_ID, "custom_sound2");
        
        CUSTOM_SOUNDS[0] = new SoundEvent(customSoundLoc).setRegistryName(customSoundLoc);
        CUSTOM_SOUNDS[1] = new SoundEvent(customSound1Loc).setRegistryName(customSound1Loc);
        CUSTOM_SOUNDS[2] = new SoundEvent(customSound2Loc).setRegistryName(customSound2Loc);

        int packetId = 0;
        NETWORK_CHANNEL.registerMessage(KissC2SPacket.Handler.class, KissC2SPacket.class, packetId++, Side.SERVER);
        NETWORK_CHANNEL.registerMessage(KissS2CPacket.Handler.class, KissS2CPacket.class, packetId++, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            KissModClient.init();

            ClientCommandHandler.instance.registerCommand(new CommandBase() {
                @Override
                public String getName() {
                    return "kissmod-rightclick";
                }

                @Override
                public String getUsage(ICommandSender sender) {
                    return "/kissmod-rightclick [true|false]";
                }
                
                @Override
                public int getRequiredPermissionLevel() {
                    return 0;
                }

                @Override
                public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
                    if (args.length > 0) {
                        boolean state = Boolean.parseBoolean(args[0]);
                        KissModClient.setRightClickEnabled(state);
                    } else {
                        KissModClient.toggleRightClickEnabled();
                    }
                    
                    String translationKey = KissModClient.rightClickEnabled ? "kissmod.toggle.enabled" : "kissmod.toggle.disabled";
                    sender.sendMessage(new TextComponentTranslation(translationKey));
                }
            });
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {}
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        for (SoundEvent sound : CUSTOM_SOUNDS) {
            event.getRegistry().register(sound);
        }
    }
} 