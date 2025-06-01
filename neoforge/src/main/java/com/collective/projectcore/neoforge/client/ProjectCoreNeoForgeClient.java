package com.collective.projectcore.neoforge.client;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.CoreBlock;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.util.UtilMethods;
import net.minecraft.client.MinecraftClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = ProjectCore.MOD_ID, dist = Dist.CLIENT)
public class ProjectCoreNeoForgeClient {

    public ProjectCoreNeoForgeClient(IEventBus modBus) {
        modBus.register(this);
    }

    @SuppressWarnings("deprecation")
    public void registerColourProviders() {
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
                );
    }

}
