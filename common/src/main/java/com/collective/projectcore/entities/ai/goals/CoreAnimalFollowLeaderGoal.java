package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;

public class CoreAnimalFollowLeaderGoal extends Goal {

    private final CoreAnimalEntity mob;
    private final Predicate<CoreAnimalEntity> targetPredicate;
    @Nullable
    private CoreAnimalEntity target;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private float minDistance;
    private float oldWaterPathFindingPenalty;
    private float maxDistance;

    public CoreAnimalFollowLeaderGoal(CoreAnimalEntity mob, double speed) {
        this.mob = mob;
        this.targetPredicate = Objects::nonNull;
        this.speed = speed;
        this.navigation = mob.getNavigation();
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(mob.getNavigation() instanceof MobNavigation) && !(mob.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    public boolean canStart() {
        if (this.mob.isChild() || this.mob.isBaby() || this.mob.getLeader().equals(this.mob.getUuidAsString())) {
            return false;
        }
        List<CoreAnimalEntity> list = this.mob.getWorld().getEntitiesByClass(CoreAnimalEntity.class, this.mob.getBoundingBox().expand(this.maxDistance), this.targetPredicate);
        if (list.isEmpty()) {
            return false;
        }
        list.removeIf(this.mob::equals);
        if (!this.mob.getLeader().isEmpty()) { // Check for leader via UUID.
            if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                if (!(serverWorld.getEntity(UUID.fromString(this.mob.getLeader())) == null)) {
                    if (serverWorld.getEntity(UUID.fromString(this.mob.getLeader())) instanceof CoreAnimalEntity wildlifeEntity) {
                        if (wildlifeEntity.isAlive()) {
                            target = wildlifeEntity;
                        }
                    }
                }
            }
        }
        if (target == null && !this.mob.getLeader().isEmpty()) { // If leader cannot be located by the method above, check for leader UUID in surrounding adults.
            for (CoreAnimalEntity target2 : list) {
                if (target2.getUuid().toString().equals(this.mob.getLeader())) {
                    target = target2;
                }
            }
        }
        if (target == null) { // If leader still can't be located, reset UUID.
            this.mob.setLeader("");
            return false;
        }
        this.minDistance = 2;
        this.maxDistance = this.mob.getMaxRoamDistance();
        return this.mob.squaredDistanceTo(this.target) > (double)(this.maxDistance * this.maxDistance);
    }

    public boolean shouldContinue() {
        if (this.mob.isChild() || this.mob.isBaby() || this.mob.getLeader().equals(this.mob.getUuidAsString())) {
            return false;
        }
        return this.target != null && !this.navigation.isIdle() && this.mob.squaredDistanceTo(this.target) > (double)(this.minDistance * this.minDistance);
    }

    public void start() {
        this.updateCountdownTicks = 0;
        this.oldWaterPathFindingPenalty = this.mob.getPathfindingPenalty(PathNodeType.WATER);
        this.mob.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public void stop() {
        this.target = null;
        this.navigation.stop();
        this.mob.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathFindingPenalty);
    }

    public void tick() {
        if (this.target != null && !this.mob.isLeashed()) {
            this.mob.getLookControl().lookAt(this.target, 10.0F, (float)this.mob.getMaxLookPitchChange());
            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = this.getTickCount(10);
                double d = this.mob.getX() - this.target.getX();
                double e = this.mob.getY() - this.target.getY();
                double f = this.mob.getZ() - this.target.getZ();
                double g = d * d + e * e + f * f;
                if (!(g <= (double)(this.minDistance * this.minDistance))) {
                    this.navigation.startMovingTo(this.target, this.speed);
                } else {
                    this.navigation.stop();
                    LookControl lookControl = this.target.getLookControl();
                    if (g <= (double)this.minDistance || lookControl.getLookX() == this.mob.getX() && lookControl.getLookY() == this.mob.getY() && lookControl.getLookZ() == this.mob.getZ()) {
                        double h = this.target.getX() - this.mob.getX();
                        double i = this.target.getZ() - this.mob.getZ();
                        this.navigation.startMovingTo(this.mob.getX() - h, this.mob.getY(), this.mob.getZ() - i, this.speed);
                    }

                }
            }
        }
    }
}
