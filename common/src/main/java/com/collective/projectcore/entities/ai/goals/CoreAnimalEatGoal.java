package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.blockentities.CoreFeederBlockEntity;
import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CoreAnimalEatGoal extends MoveToTargetPosGoal {

    private static final TargetPredicate PARTNER_TARGETING = TargetPredicate.createNonAttackable().setBaseMaxDistance(32.0).ignoreVisibility();

    protected CoreFeederBlockEntity feeder;
    CoreAnimalEntity wildlifeEntity;
    CoreAnimalEntity mother;
    int diet;
    String specialDiet;
    private float oldWaterCost;

    public CoreAnimalEatGoal(CoreAnimalEntity wildlifeEntity, int diet, String specialDiet, double pSpeedModifier, int pSearchRange) {
        super(wildlifeEntity, pSpeedModifier, pSearchRange);
        this.wildlifeEntity = wildlifeEntity;
        this.diet = diet;
        this.specialDiet = specialDiet;
    }

    @Override
    public boolean canStart() {
        if (!wildlifeEntity.isHungry()) {
            return false;
        } else if (!wildlifeEntity.isStarving()) {
            return false;
        } else {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            } else {
                this.cooldown = this.getInterval(this.mob);
                if (this.wildlifeEntity.isBaby()) {
                    mother = this.getMother(wildlifeEntity.getMotherUUID());
                    return true;
                }
                return this.findTargetPos();
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        if (wildlifeEntity.getHunger() >= wildlifeEntity.getMaxFood()) {
            return false;
        } else {
            if (wildlifeEntity.isBaby() || wildlifeEntity.isChild()) {
                if (this.mother.getHunger() <= 0) {
                    return false;
                }
                return (this.wildlifeEntity.squaredDistanceTo(this.mother) <= 2.0D);
            }
            return super.shouldContinue();
        }
    }

    @Override
    public void start() {
        if (this.wildlifeEntity.isBaby() || this.wildlifeEntity.isChild()) {
            this.oldWaterCost = this.wildlifeEntity.getPathfindingPenalty(PathNodeType.WATER);
            this.wildlifeEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        } else {
            super.start();
        }
    }

    @Override
    public void stop() {
        if (this.wildlifeEntity.isBaby() || this.wildlifeEntity.isChild()) {
            this.mother = null;
            this.wildlifeEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterCost);
        } else {
            super.stop();
        }
    }

    @Override
    public void tick() {
        if (!this.wildlifeEntity.isBaby() && !this.wildlifeEntity.isChild()) {
            super.tick();
            if (hasReached() && feeder.hasFood(wildlifeEntity)) {
                feeder.feedEntity(wildlifeEntity);
                wildlifeEntity.setHungerTicks(1600);
            }
        } else {
            this.wildlifeEntity.getLookControl().lookAt(this.mother, 10.0F, (float)this.wildlifeEntity.getMaxHeadRotation());
            this.wildlifeEntity.getNavigation().startMovingTo(this.mother, this.speed);
            if (this.wildlifeEntity.squaredDistanceTo(this.mother) < 8.0) {
                if (mother.getHunger() > 0) {
                    if (wildlifeEntity.getHunger() < wildlifeEntity.getMaxFood()) {
                        this.mother.setHunger(this.mother.getHunger() - 1);
                        if (this.wildlifeEntity.isBaby()) {
                            wildlifeEntity.setHunger(wildlifeEntity.getHunger() + 4);
                        } else {
                            wildlifeEntity.setHunger(wildlifeEntity.getHunger() + 2);
                        }
                        wildlifeEntity.getWorld().playSound(null, wildlifeEntity.getSteppingPos(), SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.NEUTRAL, 1.0F, wildlifeEntity.getPitch());
                        if (wildlifeEntity.getHunger() >= wildlifeEntity.getMaxFood()) {
                            wildlifeEntity.setHungerTicks(1600);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean isTargetPos(WorldView pLevel, @NotNull BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof CoreFeederBlockEntity) {
            feeder = (CoreFeederBlockEntity) pLevel.getBlockEntity(pPos);
            if (feeder != null) {
                return feeder.hasFood(wildlifeEntity);
            }
        }
        return false;
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return 2.0D;
    }

    public CoreAnimalEntity getMother(String motherUUID) {
        ServerWorld serverWorld = getServerWorld(this.wildlifeEntity);
        List<? extends CoreAnimalEntity> potentialParents = serverWorld.getTargets(this.wildlifeEntity.getClass(), PARTNER_TARGETING, this.wildlifeEntity, this.wildlifeEntity.getBoundingBox().expand(16.0));
        for (CoreAnimalEntity wildlifeEntity : potentialParents) {
            if (wildlifeEntity.getUuidAsString().equals(motherUUID)) {
                return wildlifeEntity;
            }
        }
        return null;
    }
}
