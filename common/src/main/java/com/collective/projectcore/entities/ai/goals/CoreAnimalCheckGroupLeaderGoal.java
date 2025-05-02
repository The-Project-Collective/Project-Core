package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.UUID;

public class CoreAnimalCheckGroupLeaderGoal extends Goal {

    private final CoreAnimalEntity animal;
    private String leader;
    private int delay;

    public CoreAnimalCheckGroupLeaderGoal(CoreAnimalEntity animal) {
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
            int oldestAge = 0;
            for (String packMemberString : this.animal.getPack()) {
                CoreAnimalEntity packMember = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(packMemberString));
                if (packMember != null && packMember.getAgeTicks() > oldestAge) {
                    oldestAge = packMember.getAgeTicks();
                    this.leader = packMember.getUuidAsString();
                }
            }
            this.animal.setLeader(this.leader);
        }
    }
}
