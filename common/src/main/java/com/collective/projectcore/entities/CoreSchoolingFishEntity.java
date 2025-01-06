package com.collective.projectcore.entities;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class CoreSchoolingFishEntity extends CoreFishEntity {

    @Nullable
    private CoreSchoolingFishEntity leader;
    private int groupSize = 1;

    protected CoreSchoolingFishEntity(EntityType<? extends AnimalEntity> entityType, World world, boolean doesAge, boolean doesBreed, boolean hasGender, boolean hasHunger, boolean canBeTamed, boolean hasVariants, boolean canBeBucketed) {
        super(entityType, world, doesAge, doesBreed, hasGender, hasHunger, canBeTamed, hasVariants, canBeBucketed);
    }

    // === TICK HANDLING =======================================================================================================================================================================

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public void tick() {
        super.tick();
        if (this.hasOtherFishInGroup() && this.getWorld().random.nextInt(200) == 1) {
            List<? extends CoreFishEntity> list = this.getWorld().getNonSpectatingEntities(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0));
            if (list.size() <= 1) {
                this.groupSize = 1;
            }
        }
    }



    // === MAIN METHODS =======================================================================================================================================================================

    // --- Aquatic ------------------------------------------------------------------------------------------
    public void joinGroupOf(CoreSchoolingFishEntity groupLeader) {
        this.leader = groupLeader;
        groupLeader.increaseGroupSize();
    }

    public void leaveGroup() {
        if (this.leader != null) {
            this.leader.decreaseGroupSize();
        }
        this.leader = null;
    }

    private void increaseGroupSize() {
        ++this.groupSize;
    }

    private void decreaseGroupSize() {
        --this.groupSize;
    }

    public void moveTowardLeader() {
        if (this.hasLeader()) {
            this.getNavigation().startMovingTo(this.leader, 1.0);
        }
    }

    public void pullInOtherFish(Stream<? extends CoreSchoolingFishEntity> fish) {
        fish.limit(this.getMaxGroupSize() - this.groupSize).filter((fishx) -> fishx != this).forEach((fishx) -> fishx.joinGroupOf(this));
    }

    // --- General ------------------------------------------------------------------------------------------
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);
        if (entityData == null) {
            entityData = new CoreFishData(this);
        } else {
            this.joinGroupOf(((CoreFishData)entityData).leader);
        }
        return entityData;
    }

    // --- Goals ------------------------------------------------------------------------------------------
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(5, new CoreFollowSchoolLeaderGoal(this));
    }



    // === GETTERS AND SETTERS =======================================================================================================================================================================

    // --- General ------------------------------------------------------------------------------------------
    protected boolean hasSelfControl() {
        return !this.hasLeader();
    }

    public boolean hasLeader() {
        return this.leader != null && this.leader.isAlive();
    }

    public boolean canHaveMoreFishInGroup() {
        return this.hasOtherFishInGroup() && this.groupSize < this.getMaxGroupSize();
    }

    public boolean hasOtherFishInGroup() {
        return this.groupSize > 1;
    }

    public boolean isCloseEnoughToLeader() {
        return this.squaredDistanceTo(this.leader) <= 121.0;
    }



    // === OVERRIDES =======================================================================================================================================================================

    // --- General ------------------------------------------------------------------------------------------
    public abstract int getMaxGroupSize();



    // === CUSTOM CLASSES =======================================================================================================================================================================

    // --- Data ------------------------------------------------------------------------------------------
    public record CoreFishData(CoreSchoolingFishEntity leader) implements EntityData {
    }

    // --- Follow Leader Goal ------------------------------------------------------------------------------------------
    public static class CoreFollowSchoolLeaderGoal extends Goal {
        private static final int MIN_SEARCH_DELAY = 200;
        private final CoreSchoolingFishEntity fish;
        private int moveDelay;
        private int checkSurroundingDelay;

        public CoreFollowSchoolLeaderGoal(CoreSchoolingFishEntity fish) {
            this.fish = fish;
            this.checkSurroundingDelay = this.getSurroundingSearchDelay(fish);
        }

        protected int getSurroundingSearchDelay(CoreSchoolingFishEntity fish) {
            return toGoalTicks(200 + fish.getRandom().nextInt(200) % 20);
        }

        public boolean canStart() {
            if (this.fish.hasOtherFishInGroup()) {
                return false;
            } else if (this.fish.hasLeader()) {
                return true;
            } else if (this.checkSurroundingDelay > 0) {
                --this.checkSurroundingDelay;
                return false;
            } else {
                this.checkSurroundingDelay = this.getSurroundingSearchDelay(this.fish);
                Predicate<CoreSchoolingFishEntity> predicate = (fish) -> fish.canHaveMoreFishInGroup() || !fish.hasLeader();
                List<? extends CoreSchoolingFishEntity> list = this.fish.getWorld().getEntitiesByClass(this.fish.getClass(), this.fish.getBoundingBox().expand(8.0, 8.0, 8.0), predicate);
                CoreSchoolingFishEntity schoolingFishEntity = DataFixUtils.orElse(list.stream().filter(CoreSchoolingFishEntity::canHaveMoreFishInGroup).findAny(), this.fish);
                schoolingFishEntity.pullInOtherFish(list.stream().filter((fish) -> !fish.hasLeader()));
                return this.fish.hasLeader();
            }
        }

        public boolean shouldContinue() {
            return this.fish.hasLeader() && this.fish.isCloseEnoughToLeader();
        }

        public void start() {
            this.moveDelay = 0;
        }

        public void stop() {
            this.fish.leaveGroup();
        }

        public void tick() {
            if (--this.moveDelay <= 0) {
                this.moveDelay = this.getTickCount(10);
                this.fish.moveTowardLeader();
            }
        }
    }
}
