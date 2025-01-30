package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CoreAnimalLeaderCombineGroupsGoal extends Goal {

    public static final int HORIZONTAL_CHECK_RANGE = 32;
    public static final int VERTICAL_CHECK_RANGE = 10;
    private final CoreAnimalEntity animal;
    private String leader;
    private List<String> pack;
    private List<? extends CoreAnimalEntity> leaderList;
    private int delay;

    public CoreAnimalLeaderCombineGroupsGoal(CoreAnimalEntity animal) {
        this.animal = animal;
    }

    @Override
    public boolean canStart() {
        if (this.animal.getPack() == null) {
            return false;
        }
        if (this.animal.isBaby() || this.animal.isChild() || !this.animal.getUuidAsString().equals(this.animal.getLeader()) || this.animal.getPack().size() >= this.animal.getMaxGroupSize()) {
            return false;
        }
        List<? extends CoreAnimalEntity> tempLeaderList = this.animal.getWorld().getNonSpectatingEntities(this.animal.getClass(), this.animal.getBoundingBox().expand(HORIZONTAL_CHECK_RANGE, VERTICAL_CHECK_RANGE, HORIZONTAL_CHECK_RANGE));
        if (tempLeaderList.isEmpty() || this.animal.getLeader() == null) {
            return false;
        }
        tempLeaderList.removeIf(animal -> animal.isBaby() ||
                animal.isChild() ||
                this.animal.getPack().contains(animal.getUuidAsString()) ||
                animal.getLeader() == null ||
                !animal.getLeader().equals(animal.getUuidAsString()) ||
                animal.getPack().size() >= animal.getMaxGroupSize() ||
                !this.canCombinePacks(this.animal.getPack().size(), animal.getPack().size()));
        leaderList = tempLeaderList;
        return !leaderList.isEmpty();
    }

    @Override
    public boolean shouldContinue() {
        if (this.animal.getPack() == null) {
            return false;
        }
        if (this.animal.isBaby() || this.animal.isChild() || !this.animal.getUuidAsString().equals(this.animal.getLeader()) || this.animal.getPack().size() >= this.animal.getMaxGroupSize()) {
            return false;
        }
        return !leaderList.isEmpty();
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
            for (CoreAnimalEntity otherLeader : leaderList) {
                if (otherLeader.getPack() != null) {
                    if (this.canCombinePacks(this.animal.getPack().size(), otherLeader.getPack().size())) {
                        List<String> newPack = new ArrayList<>(this.animal.getPack());
                        if (newPack.isEmpty()) {
                            return;
                        }
                        newPack.addAll(otherLeader.getPack());
                        this.animal.setPack(newPack);
                        for (String pack1MemberString : this.animal.getPack()) {
                            CoreAnimalEntity pack1Member = (CoreAnimalEntity) getServerWorld(this.animal).getEntity(UUID.fromString(pack1MemberString));
                            if (pack1Member != null) {
                                pack1Member.setPack(newPack);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean canCombinePacks(int packSize1, int packSize2) {
        return (packSize1 + packSize2) <= this.animal.getMaxGroupSize();
    }
}
