package com.collective.projectcore.blockentities;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blockentities.machines.FeederBlockEntity;
import com.collective.projectcore.blockentities.traps.LargeBoxTrapBlockEntity;
import com.collective.projectcore.blockentities.traps.MediumBoxTrapBlockEntity;
import com.collective.projectcore.blockentities.traps.SmallBoxTrapBlockEntity;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.util.builders.BlockEntityTypeBuilder;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;

public class CoreBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    // === HUSBANDRY ===

    // Machines
    public static final RegistrySupplier<BlockEntityType<FeederBlockEntity>> FEEDER_ENTITY = BLOCK_ENTITIES.register("feeder", () -> BlockEntityTypeBuilder.create(FeederBlockEntity::new, CoreBlocks.FEEDER.get()));

    // Traps
    public static final RegistrySupplier<BlockEntityType<SmallBoxTrapBlockEntity>> SMALL_BOX_TRAP_ENTITY = BLOCK_ENTITIES.register("small_box_trap_entity", () -> BlockEntityTypeBuilder.create(SmallBoxTrapBlockEntity::new, CoreBlocks.SMALL_BOX_TRAP.get()));
    public static final RegistrySupplier<BlockEntityType<MediumBoxTrapBlockEntity>> MEDIUM_BOX_TRAP_ENTITY = BLOCK_ENTITIES.register("medium_box_trap_entity", () -> BlockEntityTypeBuilder.create(MediumBoxTrapBlockEntity::new, CoreBlocks.MEDIUM_BOX_TRAP.get()));
    public static final RegistrySupplier<BlockEntityType<LargeBoxTrapBlockEntity>> LARGE_BOX_TRAP_ENTITY = BLOCK_ENTITIES.register("large_box_trap_entity", () -> BlockEntityTypeBuilder.create(LargeBoxTrapBlockEntity::new, CoreBlocks.LARGE_BOX_TRAP.get()));


}
