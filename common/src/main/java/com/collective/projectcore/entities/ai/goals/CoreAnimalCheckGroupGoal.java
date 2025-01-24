package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreAnimalCheckGroupGoal extends Goal {

    private final CoreAnimalEntity animal;
    private String leader;
    private int delay;

    public CoreAnimalCheckGroupGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.getPack() == null) {
            return false;
        }
        return this.animal.isAdult() || this.animal.isJuvenile();
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.getPack() == null) {
            return false;
        }
        return this.animal.isAdult() || this.animal.isJuvenile();
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void stop() {
        this.leader = null;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.getTickCount(10);
            List<String> toRemove = new ArrayList<>();
            for (String packMemberString : this.animal.getPack()) {
                CoreAnimalEntity packMember = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(packMemberString));
                if (packMember == null || !packMember.isAlive()) {
                    toRemove.add(packMemberString);
                }
            }
            List<String> newPack = this.animal.getPack();
            newPack.removeAll(toRemove);
            this.animal.setPack(newPack);
        }
    }
}
