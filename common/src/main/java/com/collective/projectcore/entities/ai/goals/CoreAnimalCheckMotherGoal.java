package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.UUID;

public class CoreAnimalCheckMotherGoal extends Goal {

    public static final int HORIZONTAL_CHECK_RANGE = 10;
    public static final int VERTICAL_CHECK_RANGE = 6;
    private final CoreAnimalEntity animal;
    private String parent;
    private int delay;

    public CoreAnimalCheckMotherGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.isJuvenile() || this.animal.isAdult()) {
            return false;
        }
        List<? extends CoreAnimalEntity> list = this.animal.getWorld().getNonSpectatingEntities(this.animal.getClass(), this.animal.getBoundingBox().expand(HORIZONTAL_CHECK_RANGE, VERTICAL_CHECK_RANGE, HORIZONTAL_CHECK_RANGE));
        if (list.isEmpty()) {
            return false;
        }
        list.removeIf(this.animal::equals);
        list.removeIf(animal -> !animal.isAdult());
        list.removeIf(animal -> animal.getGender() == 0);
        String animalEntity = "";
        double d = Double.MAX_VALUE;
        if (!this.animal.getMotherUUID().isEmpty()) {
            if (this.animal.getWorld() instanceof ServerWorld serverWorld) {
                if (serverWorld.getEntity(UUID.fromString(this.animal.getMotherUUID())) instanceof CoreAnimalEntity wildlifeEntity) {
                    if (wildlifeEntity.isAlive()) {
                        animalEntity = wildlifeEntity.getUuidAsString();
                    }
                }

            }
        } else {
            for (CoreAnimalEntity animalEntity2 : list) {
                double e = this.animal.squaredDistanceTo(animalEntity2);
                if (!(e > d)) {
                    d = e;
                    animalEntity = animalEntity2.getUuidAsString();
                }
            }
        }
        this.parent = animalEntity;
        return !this.parent.equals(this.animal.getMotherUUID());
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isJuvenile() || this.animal.isAdult()) {
            return false;
        }
        return !this.parent.equals(this.animal.getMotherUUID());
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
            this.animal.setMotherUUID(this.parent);
        }
    }
}
