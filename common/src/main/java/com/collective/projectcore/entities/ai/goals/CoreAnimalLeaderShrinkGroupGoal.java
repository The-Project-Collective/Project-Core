package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreAnimalLeaderShrinkGroupGoal extends Goal {

    private final CoreAnimalEntity animal;
    private int delay;
    private int adultPackSize;

    public CoreAnimalLeaderShrinkGroupGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.isBaby() || this.animal.isChild() || this.animal.getPack() == null) {
            return false;
        }
        this.calculateAdultPackSize();
        return this.animal.getUuidAsString().equals(this.animal.getLeader()) && adultPackSize > this.animal.getMaxGroupSize();
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.isBaby() || this.animal.isChild() || this.animal.getPack() == null) {
            return false;
        }
        calculateAdultPackSize();
        return this.animal.getUuidAsString().equals(this.animal.getLeader()) && adultPackSize > this.animal.getMaxGroupSize();
    }

    @Override
    public void start() {
        this.delay = 0;
    }

    @Override
    public void tick() {
        if (--this.delay <= 0) {
            this.delay = this.getTickCount(10);
            int youngestAge = 0;
            List<String> currentPack = new ArrayList<>(this.animal.getPack());
            CoreAnimalEntity youngestMember = null;
            for (String packMember : currentPack) {
                CoreAnimalEntity packMemberEntity = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(packMember));
                if (packMemberEntity != null) {
                    if (youngestAge == 0 || packMemberEntity.getAgeTicks() < youngestAge) {
                        youngestAge = packMemberEntity.getAgeTicks();
                        youngestMember = packMemberEntity;
                    }
                }
            }
            if (youngestMember != null) {
                currentPack.remove(youngestMember.getUuidAsString());
                youngestMember.setPack(List.of(youngestMember.getUuidAsString()));
                youngestMember.setLeader(youngestMember.getUuidAsString());
                this.animal.setPack(currentPack);
                for (String packMemberString : this.animal.getPack()) {
                    CoreAnimalEntity packMember = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(packMemberString));
                    if (packMember != null) {
                        packMember.setPack(currentPack);
                    }
                }
            }
            calculateAdultPackSize();
        }
    }

    public void calculateAdultPackSize() {
        List<String> adultPack = new ArrayList<>(this.animal.getPack());
        List<CoreAnimalEntity> adultPackEntities = new ArrayList<>();
        for (String packMember: adultPack) {
            adultPackEntities.add((CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(packMember)));
        }
        if (!adultPackEntities.isEmpty()) {
            adultPackEntities.removeIf(packMember -> packMember == null || !packMember.isAlive() || !packMember.isAdult());
            adultPackSize = adultPackEntities.size();
        } else {
            adultPackSize = this.animal.getPack().size();
        }
    }
}
