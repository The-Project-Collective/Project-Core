package com.collective.projectcore.entities;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.entities.enrichment.SnuffleLogEnrichmentEntity;
import com.collective.projectcore.entities.enrichment.ToyBallEnrichmentEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class CoreEntities {

    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ENTITY_TYPE);

    // === ENTITIES ===

    // Enrichment
    public static final RegistrySupplier<EntityType<SnuffleLogEnrichmentEntity>> SNUFFLE_LOG_ENTITY = ENTITIES.register("snuffle_log_entity",
            () -> EntityType.Builder.create(SnuffleLogEnrichmentEntity::new, SpawnGroup.MISC)
                    .dimensions(0.8f, 0.55f)
                    .maxTrackingRange(8)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "snuffle_log_entity")))
    );
    public static final RegistrySupplier<EntityType<ToyBallEnrichmentEntity>> TOY_BALL_ENTITY = ENTITIES.register("toy_ball_entity",
            () -> EntityType.Builder.create(ToyBallEnrichmentEntity::new, SpawnGroup.MISC)
                    .dimensions(0.7f, 0.7f)
                    .maxTrackingRange(8)
                    .build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "toy_ball_entity")))
    );
}
