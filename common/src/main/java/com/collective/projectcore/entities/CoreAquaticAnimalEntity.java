package com.collective.projectcore.entities;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterAnimalEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class CoreAquaticAnimalEntity extends CoreAnimalEntity {

    protected CoreAquaticAnimalEntity(EntityType<? extends AnimalEntity> entityType, World world,
                                      boolean doesAge, boolean getsAngry, boolean doesBreed, boolean hasEnrichment, boolean hasGender, boolean hasHunger, boolean hasAPack, boolean canBeTamed, boolean hasVariants) {
        super(entityType, world, doesAge, getsAngry, doesBreed, hasEnrichment, hasGender, hasHunger, hasAPack, canBeTamed, hasVariants);
        this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    // === TICK HANDLING =======================================================================================================================================================================

    // --- Aquatic Ticker ------------------------------------------------------------------------------------------
    public void baseTick() {
        int i = this.getAir();
        super.baseTick();
        this.tickBreathing(i);
    }

    // === MAIN METHODS =======================================================================================================================================================================

    // --- Aquatic Breathing ------------------------------------------------------------------------------------------
    /**
     * Controls the breathing for underwater animals.
     * Override this method and leave it empty for aquatic animals that live in water but breathe air.
     * @param air amount for the entity at that tick in time.
     */
    @SuppressWarnings("deprecation")
    protected void tickBreathing(int air) {
        if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
            this.setAir(air - 1);
            if (this.getAir() == -20) {
                this.setAir(0);
                this.serverDamage(this.getDamageSources().drown(), 2.0F);
            }
        } else {
            this.setAir(300);
        }
    }

    // === GETTERS AND SETTERS =======================================================================================================================================================================

    // --- Aquatic  ------------------------------------------------------------------------------------------
    public boolean canSpawn(WorldView world) {
        return world.doesNotIntersectEntities(this);
    }

    public int getMinAmbientSoundDelay() {
        return 120;
    }

    public int getExperienceToDrop(ServerWorld world) {
        return 1 + this.random.nextInt(3);
    }

    public boolean isPushedByFluids() {
        return false;
    }

    public boolean canBeLeashed() {
        return false;
    }

    public static boolean canSpawn(EntityType<? extends WaterAnimalEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
        int i = world.getSeaLevel();
        int j = i - 13;
        return pos.getY() >= j && pos.getY() <= i && world.getFluidState(pos.down()).isIn(FluidTags.WATER) && world.getBlockState(pos.up()).isOf(Blocks.WATER);
    }

}
