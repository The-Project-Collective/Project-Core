package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreAnimalParentCheckOffspringGoal extends Goal {

    private final CoreAnimalEntity animal;
    private int delay;

    public CoreAnimalParentCheckOffspringGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.isAdult() && this.animal.isParent()) {
            return this.animal.getOffspring() != null && !this.animal.getOffspring().isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isAdult() && this.animal.isParent()) {
            return this.animal.getOffspring() != null && !this.animal.getOffspring().isEmpty();
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            List<String> offspringList = this.animal.getOffspring();
            List<String> toRemove = new ArrayList<>();
            for (String offspring : offspringList) {
                CoreAnimalEntity offspringEntity = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(offspring));
                if (offspringEntity != null && offspringEntity.isAlive()) {
                    if (offspringEntity.isJuvenile() || offspringEntity.isAdult()) {
                        toRemove.add(offspring);
                    }
                } else {
                    toRemove.add(offspring);
                }
            }
            if (!toRemove.isEmpty()) {
                offspringList.removeAll(toRemove);
            }
            this.animal.setOffspring(offspringList);
        }
    }
}
