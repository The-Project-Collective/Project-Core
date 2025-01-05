package com.collective.projectcore.entities.base;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;

public abstract class CoreBaseEntity extends PathAwareEntity {

    protected CoreBaseEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }
}
