package com.collective.projectcore.fabric.client;

import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.util.UtilMethods;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public final class ProjectCoreFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        registerColourProviders();
    }

    public void registerColourProviders() {
        ColorProviderRegistry.BLOCK.register(
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
                CoreBlocks.SCRATCHING_POST_WARPED.get());
    }
}
