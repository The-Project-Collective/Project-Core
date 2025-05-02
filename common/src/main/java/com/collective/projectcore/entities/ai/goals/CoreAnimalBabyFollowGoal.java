package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CoreAnimalBabyFollowGoal extends Goal {

    public static final int HORIZONTAL_CHECK_RANGE = 10;
    public static final int VERTICAL_CHECK_RANGE = 6;
    public static final int BABY_MIN_DISTANCE = 1;
    public static final int CHILD_MIN_DISTANCE = 3;
    public double distanceSquared = 1;
    private final CoreAnimalEntity animal;
    @Nullable
    private CoreAnimalEntity parent;
    private final double speed;
    private int delay;

    public CoreAnimalBabyFollowGoal(CoreAnimalEntity animal, double speed) {
        this.animal = animal;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        if (this.animal.isJuvenile() || this.animal.isAdult()) {
            return false;
        } else {
            List<? extends CoreAnimalEntity> list = this.animal.getWorld().getNonSpectatingEntities(this.animal.getClass(), this.animal.getBoundingBox().expand(HORIZONTAL_CHECK_RANGE, VERTICAL_CHECK_RANGE, HORIZONTAL_CHECK_RANGE));
            if (list.isEmpty()) {
                return false;
            }
            list.removeIf(this.animal::equals);
            CoreAnimalEntity animalEntity = null;
            double d = Double.MAX_VALUE;
            if (this.animal.isBaby()) {
                distanceSquared = BABY_MIN_DISTANCE * BABY_MIN_DISTANCE;
            } else {
                distanceSquared = CHILD_MIN_DISTANCE * CHILD_MIN_DISTANCE;
            }
            if (!this.animal.getMotherUUID().isEmpty()) { // Check for mother via UUID.
                if (this.animal.getWorld() instanceof ServerWorld serverWorld) {
                    if (!(serverWorld.getEntity(UUID.fromString(this.animal.getMotherUUID())) == null)) {
                        if (serverWorld.getEntity(UUID.fromString(this.animal.getMotherUUID())) instanceof CoreAnimalEntity wildlifeEntity) {
                            if (wildlifeEntity.isAlive()) {
                                animalEntity = wildlifeEntity;
                            }
                        }
                    }
                }
            }
            if (animalEntity == null && !this.animal.getMotherUUID().isEmpty()) { // If mother cannot be located by the method above, check for mother UUID in surrounding adults.
                for (CoreAnimalEntity animalEntity2 : list) {
                    if (animalEntity2.getUuid().toString().equals(this.animal.getMotherUUID())) {
                        double e = this.animal.squaredDistanceTo(animalEntity2);
                        if (!(e > d)) {
                            d = e;
                            animalEntity = animalEntity2;
                            break;
                        }
                    }
                }
            }
            if (animalEntity == null) { // If mother still can't be located, reset UUID.
                this.animal.setMotherUUID("");
            }
            if (animalEntity == null) { // If no adult female around to become mother, follow the next oldest group member.
                int age = 0;
                for (CoreAnimalEntity animalEntity2 : list) {
                    if (animalEntity2.getAgeTicks() > age) {
                        double e = this.animal.squaredDistanceTo(animalEntity2);
                        if (!(e > d)) {
                            d = e;
                            age = animalEntity2.getAgeTicks();
                            animalEntity = animalEntity2;
                        }
                    }
                }
            }
            if (animalEntity == null) {
                return false;
            } else if (d < distanceSquared) {
                return false;
            } else {
                this.parent = animalEntity;
                return true;
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isJuvenile() || this.animal.isAdult()) {
            return false;
        } else if (!Objects.requireNonNull(this.parent).isAlive()) {
            return false;
        } else {
            double d = this.animal.squaredDistanceTo(this.parent);
            return !(d < distanceSquared) && !(d > 256.0);
        }
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void stop() {
        this.parent = null;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.getTickCount(10);
            this.animal.getNavigation().startMovingTo(this.parent, this.speed);
        }
    }

}
