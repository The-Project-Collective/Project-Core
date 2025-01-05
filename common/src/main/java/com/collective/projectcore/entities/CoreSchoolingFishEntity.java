package com.collective.projectcore.entities;

import com.collective.projectcore.entities.ai.CoreFollowFishLeaderGoal;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public abstract class CoreSchoolingFishEntity extends CoreFishEntity {

    @Nullable
    private CoreSchoolingFishEntity leader;
    private int groupSize = 1;

    public CoreSchoolingFishEntity(EntityType<? extends CoreSchoolingFishEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(5, new CoreFollowFishLeaderGoal(this));
    }

    public int getLimitPerChunk() {
        return this.getMaxGroupSize();
    }

    public int getMaxGroupSize() {
        return super.getLimitPerChunk();
    }

    protected boolean hasSelfControl() {
        return !this.hasLeader();
    }

    public boolean hasLeader() {
        return this.leader != null && this.leader.isAlive();
    }

    public CoreSchoolingFishEntity joinGroupOf(CoreSchoolingFishEntity groupLeader) {
        this.leader = groupLeader;
        groupLeader.increaseGroupSize();
        return groupLeader;
    }

    public void leaveGroup() {
        this.leader.decreaseGroupSize();
        this.leader = null;
    }

    private void increaseGroupSize() {
        ++this.groupSize;
    }

    private void decreaseGroupSize() {
        --this.groupSize;
    }

    public boolean canHaveMoreFishInGroup() {
        return this.hasOtherFishInGroup() && this.groupSize < this.getMaxGroupSize();
    }

    public void tick() {
        super.tick();
        if (this.hasOtherFishInGroup() && this.getWorld().random.nextInt(200) == 1) {
            List<? extends CoreSchoolingFishEntity> list = this.getWorld().getNonSpectatingEntities(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0));
            if (list.size() <= 1) {
                this.groupSize = 1;
            }
        }

    }

    public boolean hasOtherFishInGroup() {
        return this.groupSize > 1;
    }

    public boolean isCloseEnoughToLeader() {
        return this.squaredDistanceTo(this.leader) <= 121.0;
    }

    public void moveTowardLeader() {
        if (this.hasLeader()) {
            this.getNavigation().startMovingTo(this.leader, 1.0);
        }

    }

    public void pullInOtherFish(Stream<? extends CoreSchoolingFishEntity> fish) {
        fish.limit((long)(this.getMaxGroupSize() - this.groupSize)).filter((fishx) -> {
            return fishx != this;
        }).forEach((fishx) -> {
            fishx.joinGroupOf(this);
        });
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        super.initialize(world, difficulty, spawnReason, entityData);
        if (entityData == null) {
            entityData = new CoreSchoolingFishEntity.FishData(this);
        } else {
            this.joinGroupOf(((CoreSchoolingFishEntity.FishData)entityData).leader);
        }

        return (EntityData)entityData;
    }

    public static class FishData implements EntityData {
        public final CoreSchoolingFishEntity leader;

        public FishData(CoreSchoolingFishEntity leader) {
            this.leader = leader;
        }
    }
}
