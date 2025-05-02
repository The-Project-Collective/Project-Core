package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class CoreAnimalMateCheckGoal extends Goal {

    private final CoreAnimalEntity animal;
    private int delay;

    public CoreAnimalMateCheckGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        return this.animal.isAdult() && !this.animal.getMateUUID().isEmpty();
    }

    @Override
    public boolean shouldContinue() {
        return this.animal.isAdult() && !this.animal.getMateUUID().isEmpty();
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.getTickCount(10);
            if (this.animal.getWorld() instanceof ServerWorld serverWorld) {
                if (serverWorld.getEntity(UUID.fromString(this.animal.getMateUUID())) instanceof CoreAnimalEntity wildlifeEntity) {
                    if (!wildlifeEntity.isAlive()) {
                        this.animal.setMateUUID("");
                    }
                }

            }
        }
    }
}
