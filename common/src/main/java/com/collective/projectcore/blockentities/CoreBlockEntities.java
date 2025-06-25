package com.collective.projectcore.blockentities;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.util.builders.BlockEntityTypeBuilder;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;

public class CoreBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    // Machines

    public static final RegistrySupplier<BlockEntityType<FeederBlockEntity>> FEEDER_ENTITY = BLOCK_ENTITIES.register("feeder", () -> BlockEntityTypeBuilder.create(FeederBlockEntity::new, CoreBlocks.FEEDER.get()));

}
