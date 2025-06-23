package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.blockentities.CoreFeederBlockEntity;
import com.collective.projectcore.entities.CoreAnimalEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

public class CoreAnimalEatGoal extends Goal {

    protected final CoreAnimalEntity mob;
    public final double speed;
    protected int tryingTime;
    private int safeWaitingTime;
    protected BlockPos targetPos;
    private boolean reached;
    private final int range;
    private final int maxYDifference;
    protected int lowestY;

    private static final TargetPredicate PARTNER_TARGETING = TargetPredicate.createNonAttackable().setBaseMaxDistance(32.0).ignoreVisibility();

    protected CoreFeederBlockEntity feeder;
    CoreAnimalEntity wildlifeEntity;
    CoreAnimalEntity mother;
    private float oldWaterCost;

    public CoreAnimalEatGoal(CoreAnimalEntity wildlifeEntity, double pSpeedModifier, int pSearchRange, int maxYHeight) {
        this.wildlifeEntity = wildlifeEntity;
        this.targetPos = BlockPos.ORIGIN;
        this.mob = wildlifeEntity;
        this.speed = pSpeedModifier;
        this.range = pSearchRange;
        this.lowestY = 0;
        this.maxYDifference = maxYHeight;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if ((wildlifeEntity.isHungry() && !wildlifeEntity.isSleeping()) || (wildlifeEntity.isHungry() && !wildlifeEntity.isResting()) || wildlifeEntity.isStarving() || (!wildlifeEntity.isFull() && wildlifeEntity.isBaby())) {
            if (this.wildlifeEntity.isBaby()) {
                mother = this.getMother(wildlifeEntity.getMotherUUID());
                return true;
            }
            if (feeder != null) {
                this.targetPos = feeder.getPos();
                return true;
            } else {
                return this.findTargetPos();
            }
        } else {
            return false;
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
            return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= 1200 && this.isTargetPos(this.mob.getWorld(), this.targetPos);
        }
    }

    @Override
    public void start() {
        if (this.wildlifeEntity.isResting() && this.wildlifeEntity.isStarving()) {
            this.wildlifeEntity.setRestingTicks(0);
        }
        if (this.wildlifeEntity.isBaby() || this.wildlifeEntity.isChild()) {
            this.oldWaterCost = this.wildlifeEntity.getPathfindingPenalty(PathNodeType.WATER);
            this.wildlifeEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        } else {
            this.startMovingToTarget();
            this.tryingTime = 0;
            this.safeWaitingTime = this.mob.getRandom().nextInt(this.mob.getRandom().nextInt(1200) + 1200) + 1200;
        }
    }

    protected void startMovingToTarget() {
        this.mob.getNavigation().startMovingTo((double)this.targetPos.getX() + 0.5, this.targetPos.getY() + 1, (double)this.targetPos.getZ() + 0.5, this.speed);
    }

    @Override
    public void stop() {
        if (this.wildlifeEntity.isBaby() || this.wildlifeEntity.isChild()) {
            this.mother = null;
            this.wildlifeEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterCost);
            if (wildlifeEntity.firstFeed) {
                wildlifeEntity.setTirednessTicks(0);
            }
        } else {
            super.stop();
        }
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public boolean shouldResetPath() {
        return this.tryingTime % 40 == 0;
    }

    protected boolean hasReached() {
        return this.reached;
    }

    @Override
    public void tick() {
        if (!this.wildlifeEntity.isBaby() && !this.wildlifeEntity.isChild()) {
            BlockPos blockPos = this.getTargetPos();
            if (!blockPos.isWithinDistance(this.mob.getPos(), this.getDesiredDistanceToTarget())) {
                this.reached = false;
                ++this.tryingTime;
                if (this.shouldResetPath()) {
                    this.mob.getNavigation().startMovingTo((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, this.speed);
                }
            } else {
                this.reached = true;
                --this.tryingTime;
            }
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
                        wildlifeEntity.setHungerTicks(1600);
                        wildlifeEntity.getWorld().playSound(null, wildlifeEntity.getSteppingPos(), SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.NEUTRAL, 1.0F, wildlifeEntity.getPitch());
                        if (wildlifeEntity.getHunger() >= wildlifeEntity.getMaxFood()) {
                            wildlifeEntity.setHunger(wildlifeEntity.getMaxFood());
                        }
                    }
                }
            }
        }
    }

    protected boolean findTargetPos() {
        int i = this.range;
        int j = this.maxYDifference;
        BlockPos blockPos = this.mob.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for(int k = this.lowestY; k <= j; k = k > 0 ? -k : 1 - k) {
            for(int l = 0; l < i; ++l) {
                for(int m = 0; m <= l; m = m > 0 ? -m : 1 - m) {
                    for(int n = m < l && m > -l ? l : 0; n <= l; n = n > 0 ? -n : 1 - n) {
                        mutable.set(blockPos, m, k - 1, n);
                        if (this.mob.isInWalkTargetRange(mutable) && this.isTargetPos(this.mob.getWorld(), mutable)) {
                            this.targetPos = mutable;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    protected BlockPos getTargetPos() {
        return new BlockPos(this.targetPos.getX() + 1, this.targetPos.getY(), this.targetPos.getZ() + 1);
    }

    protected boolean isTargetPos(WorldView pLevel, @NotNull BlockPos pPos) {
        if (pLevel.getBlockEntity(pPos) instanceof CoreFeederBlockEntity) {
            feeder = (CoreFeederBlockEntity) pLevel.getBlockEntity(pPos);
            if (feeder != null) {
                return feeder.hasFood(wildlifeEntity);
            }
        }
        return false;
    }

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
