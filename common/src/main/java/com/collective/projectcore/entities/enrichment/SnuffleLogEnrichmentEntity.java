package com.collective.projectcore.entities.enrichment;

import com.collective.projectcore.items.CoreItems;
import net.minecraft.entity.*;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class SnuffleLogEnrichmentEntity extends CoreEnrichmentEntity {

    public SnuffleLogEnrichmentEntity(EntityType<? extends CoreEnrichmentEntity> type, World world) {
        super(type, world);
    }

    @Override
    public Item getItemForEnrichmentType(int type) {
        EnrichmentLogType logType = EnrichmentLogType.fromId(type);
        return switch (logType) {
            case ACACIA -> CoreItems.SNUFFLE_LOG_ACACIA.get();
            case BIRCH -> CoreItems.SNUFFLE_LOG_BIRCH.get();
            case CHERRY -> CoreItems.SNUFFLE_LOG_CHERRY.get();
            case CRIMSON -> CoreItems.SNUFFLE_LOG_CRIMSON.get();
            case DARK_OAK -> CoreItems.SNUFFLE_LOG_DARK_OAK.get();
            case JUNGLE -> CoreItems.SNUFFLE_LOG_JUNGLE.get();
            case MANGROVE -> CoreItems.SNUFFLE_LOG_MANGROVE.get();
            case OAK -> CoreItems.SNUFFLE_LOG_OAK.get();
            case PALE_OAK -> CoreItems.SNUFFLE_LOG_PALE_OAK.get();
            case SPRUCE -> CoreItems.SNUFFLE_LOG_SPRUCE.get();
            case WARPED -> CoreItems.SNUFFLE_LOG_WARPED.get();
        };
    }


}
