package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class CoreAnimalBreedGoal extends Goal {

    private static final TargetPredicate VALID_MATE_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility();
    protected final CoreAnimalEntity animal;
    private final Class<? extends CoreAnimalEntity> entityClass;
    protected final ServerWorld world;
    protected CoreAnimalEntity mate;
    private int timer;
    private final double speed;

    public CoreAnimalBreedGoal(CoreAnimalEntity animal, double speed) {
        this(animal, speed, animal.getClass());
    }

    public CoreAnimalBreedGoal(CoreAnimalEntity animal, double speed, Class<? extends CoreAnimalEntity> entityClass) {
        this.animal = animal;
        this.world = getServerWorld(animal);
        this.entityClass = entityClass;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.animal.isPregnant() || this.animal.isParent() || !this.animal.isAdult() || this.animal.getBreedingTicks() > 0) {
            return false;
        }
        this.mate = this.findMate();
        return this.mate != null;
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isPregnant() || this.animal.isParent() || !this.animal.isAdult() || this.animal.getBreedingTicks() > 0) {
            return false;
        }
        if (this.mate.isPregnant() || this.mate.isParent() || !this.mate.isAdult() || this.mate.getBreedingTicks() > 0) {
            return false;
        }
        return this.mate.isAlive() && this.timer < 60 && !this.mate.isPanicking() || this.mate.isAdult();
    }

    @Override
    public void stop() {
        this.mate = null;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().lookAt(this.mate, 10.0f, this.animal.getMaxLookPitchChange());
        this.animal.getNavigation().startMovingTo(this.mate, this.speed);
        ++this.timer;
        if (this.timer >= this.getTickCount(60) && this.animal.squaredDistanceTo(this.mate) < 9.0) {
            this.breed();
        }
    }

    private CoreAnimalEntity findMate() {
        List<? extends CoreAnimalEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.animal, this.animal.getBoundingBox().expand(8.0));
        double d = Double.MAX_VALUE;
        CoreAnimalEntity animalEntity = null;
        if (!this.animal.getMateUUID().isEmpty()) {
            if (this.animal.getWorld() instanceof ServerWorld serverWorld) {
                if (serverWorld.getEntity(UUID.fromString(this.animal.getMateUUID())) instanceof CoreAnimalEntity wildlifeEntity) {
                    if (wildlifeEntity.isAlive() && this.animal.canBreedWith(wildlifeEntity)) {
                        return wildlifeEntity;
                    }
                }
            }
        }
        for (CoreAnimalEntity animalEntity2 : list) {
            if (!this.animal.canBreedWith(animalEntity2) || animalEntity2.isPanicking() || !(this.animal.squaredDistanceTo(animalEntity2) < d)) continue;
            animalEntity = animalEntity2;
            d = this.animal.squaredDistanceTo(animalEntity2);
        }
        return animalEntity;
    }

    protected void breed() {
        this.animal.breed(this.world, this.mate);
    }
}
