package com.collective.projectcore.fabric.client;

import com.collective.projectcore.ProjectCoreClientCommon;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.entities.CoreEntities;
import com.collective.projectcore.models.entity.enrichment.SnuffleLogEnrichmentEntityModel;
import com.collective.projectcore.models.entity.enrichment.ToyBallEnrichmentEntityModel;
import com.collective.projectcore.renderers.entity.SnuffleLogEnrichmentEntityRenderer;
import com.collective.projectcore.renderers.entity.ToyBallEnrichmentEntityRenderer;
import com.collective.projectcore.screens.handlers.CoreScreenHandlers;
import com.collective.projectcore.screens.machines.FeederScreen;
import com.collective.projectcore.util.UtilMethods;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public final class ProjectCoreFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerModelLayers();
        registerEntityRenderers();
        registerColourProviders();
        registerScreens();
        ProjectCoreClientCommon.registerCommonClient();
    }

    public static void registerModelLayers() {
        EntityModelLayerRegistry.registerModelLayer(SnuffleLogEnrichmentEntityModel.LAYER_LOCATION, SnuffleLogEnrichmentEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ToyBallEnrichmentEntityModel.LAYER_LOCATION, ToyBallEnrichmentEntityModel::getTexturedModelData);
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(CoreEntities.SNUFFLE_LOG_ENTITY.get(), SnuffleLogEnrichmentEntityRenderer::new);
        EntityRendererRegistry.register(CoreEntities.TOY_BALL_ENTITY.get(), ToyBallEnrichmentEntityRenderer::new);
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

    public static void registerScreens() {
        if (CoreScreenHandlers.SCREEN_HANDLERS.getRegistrar() != null) {
            if (CoreScreenHandlers.FEEDER_SCREEN_HANDLER.isPresent()) {
                MenuRegistry.registerScreenFactory(CoreScreenHandlers.FEEDER_SCREEN_HANDLER.get(), FeederScreen::new);
            }
        }
    }
}
