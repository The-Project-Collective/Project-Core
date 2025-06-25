package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;

public class CoreAnimalWanderFarGoal extends WanderAroundFarGoal {

    CoreAnimalEntity animal;

    public CoreAnimalWanderFarGoal(CoreAnimalEntity animal, double d) {
        super(animal, d);
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.isSleeping() || this.animal.isResting()) {
            return false;
        }
        return super.canStart();

    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isSleeping() || this.animal.isResting()) {
            return false;
        }
        return super.shouldContinue();
    }
}
