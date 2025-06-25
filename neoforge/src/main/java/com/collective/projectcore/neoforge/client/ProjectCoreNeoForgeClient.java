package com.collective.projectcore.neoforge.client;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.screens.handlers.CoreScreenHandlers;
import com.collective.projectcore.screens.machines.FeederScreen;
import com.collective.projectcore.util.UtilMethods;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = ProjectCore.MOD_ID, dist = Dist.CLIENT)
public class ProjectCoreNeoForgeClient {

    public ProjectCoreNeoForgeClient(IEventBus modBus) {
        modBus.register(this);
    }

    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MinecraftClient.getInstance().getBlockColors().registerColorProvider(
                        UtilMethods::getBlockColour,
                        CoreBlocks.SCRATCHING_POST_ACACIA.get(),
                        CoreBlocks.SCRATCHING_POST_BIRCH.get(),
                        CoreBlocks.SCRATCHING_POST_CHERRY.get(),
                        CoreBlocks.SCRATCHING_POST_CRIMSON.get(),
                        CoreBlocks.SCRATCHING_POST_DARK_OAK.get(),
                        CoreBlocks.SCRATCHING_POST_JUNGLE.get(),
                        CoreBlocks.SCRATCHING_POST_MANGROVE.get(),
                        CoreBlocks.SCRATCHING_POST_OAK.get(),
                        CoreBlocks.SCRATCHING_POST_PALE_OAK.get(),
                        CoreBlocks.SCRATCHING_POST_SPRUCE.get(),
                        CoreBlocks.SCRATCHING_POST_WARPED.get()
                )
        );
    }

    @SubscribeEvent
    public void registerMenuScreensEvent(RegisterMenuScreensEvent event) {
        event.register(CoreScreenHandlers.FEEDER_SCREEN_HANDLER.get(), FeederScreen::new);

    }

}
