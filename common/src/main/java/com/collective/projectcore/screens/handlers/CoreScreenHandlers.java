package com.collective.projectcore.screens.handlers;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.screens.handlers.machines.FeederScreenHandler;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;

public class CoreScreenHandlers {

    public static final DeferredRegister<ScreenHandlerType<?>> SCREEN_HANDLERS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.SCREEN_HANDLER);

    // === BLOCKS ===

    // Machines
    public static final RegistrySupplier<ScreenHandlerType<FeederScreenHandler>> FEEDER_SCREEN_HANDLER =
            SCREEN_HANDLERS.register("feeder", () -> new ScreenHandlerType<>(FeederScreenHandler::new, FeatureFlags.DEFAULT_ENABLED_FEATURES));
}
