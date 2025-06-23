package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;

public class CoreAnimalLookAroundGoal extends LookAroundGoal {

    CoreAnimalEntity animal;

    public CoreAnimalLookAroundGoal(CoreAnimalEntity mob) {
        super(mob);
        this.animal = mob;
    }

    @Override
    public boolean canStart() {
        if (this.animal.isSleeping()) {
            return false;
        }
        return super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isSleeping()) {
            return false;
        }
        return super.shouldContinue();
    }
}
