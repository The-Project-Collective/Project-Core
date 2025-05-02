package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FleeEntityGoal;

import java.util.List;

public class CoreAnimalAvoidEnemyPackGoal<T extends LivingEntity> extends FleeEntityGoal<T> {

    private final CoreAnimalEntity animal;

    public CoreAnimalAvoidEnemyPackGoal(final CoreAnimalEntity animal, final Class<T> fleeFromType, final float distance, final double slowSpeed, final double fastSpeed) {
        super(animal, fleeFromType, distance, slowSpeed, fastSpeed);
        this.animal = animal;
    }

    public boolean canStart() {
        if (super.canStart() && this.targetEntity instanceof CoreAnimalEntity) {
            return !this.isInPack((CoreAnimalEntity) this.targetEntity, this.animal.getPack());
        } else {
            return false;
        }
    }

    public void start() {
        this.animal.setTarget(null);
        super.start();
    }

    public void tick() {
        this.animal.setTarget(null);
        super.tick();
    }

    public boolean isInPack(CoreAnimalEntity enemy, List<String> pack) {
        for (String packUUID : pack) {
            if (enemy.getUuidAsString().equals(packUUID)) {
                return true;
            }
        }
        return false;
    }

}
