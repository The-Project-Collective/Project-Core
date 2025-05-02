package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreAnimalMotherCheckBabiesPackGoal extends Goal {

    private final CoreAnimalEntity animal;
    private int delay;

    public CoreAnimalMotherCheckBabiesPackGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.getPack() == null) {
            return false;
        }
        if (this.animal.isBaby() || this.animal.isChild() || this.animal.isJuvenile()) {
            return this.animal.getMotherUUID() != null;
        } else {
            return false;
        }    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.getPack() == null) {
            return false;
        }
        if (this.animal.isBaby() || this.animal.isChild() || this.animal.isJuvenile()) {
            return this.animal.getMotherUUID() != null;
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
            this.delay = this.getTickCount(10);
            if (this.animal.getMotherUUID() != null && !this.animal.getMotherUUID().isEmpty()) {
                CoreAnimalEntity mother = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(this.animal.getMotherUUID()));
                if (mother != null && mother.isAlive()) {
                    if (mother.getPack() != null && !mother.getPack().isEmpty()) {
                        List<String> motherPack = new ArrayList<>(mother.getPack());
                        motherPack.add(this.animal.getUuidAsString());
                        mother.setPack(motherPack);
                        for (String motherPackMember : mother.getPack()) {
                            CoreAnimalEntity motherPackMemberEntity = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(motherPackMember));
                            if (motherPackMemberEntity != null && motherPackMemberEntity.isAlive()) {
                                motherPackMemberEntity.setPack(motherPack);
                            }
                        }
                        this.animal.setPack(motherPack);
                        this.animal.setLeader(mother.getLeader());
                    }
                }
            }
        }
    }
}
