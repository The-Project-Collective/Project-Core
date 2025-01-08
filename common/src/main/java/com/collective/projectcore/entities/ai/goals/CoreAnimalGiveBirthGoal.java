package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.entities.base.CoreAnimalEntity;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class CoreAnimalGiveBirthGoal extends MoveToTargetPosGoal {

    CoreAnimalEntity wildlifeEntity;
    Block homeBlock;
    Random random = new Random();

    public CoreAnimalGiveBirthGoal(CoreAnimalEntity wildlifeEntity, double pSpeedModifier, int pSearchRange) {
        super(wildlifeEntity, pSpeedModifier, pSearchRange);
        this.wildlifeEntity = wildlifeEntity;
    }

    @Override
    public boolean canStart() {
        if (!wildlifeEntity.isPregnant() || wildlifeEntity.getPregnancyTicks() > 1) {
            return false;
        } else {
            return super.canStart();
        }
    }

    @Override
    public boolean shouldContinue() {
        if (!wildlifeEntity.isPregnant() || wildlifeEntity.getPregnancyTicks() > 1) {
            return false;
        } else {
            return super.shouldContinue();
        }
    }

    @Override
    public void tick() {
        if (!this.wildlifeEntity.getWorld().isClient()) {
            super.tick();
            if (hasReached()) {
                this.mob.getNavigation().stop();
                if (wildlifeEntity.isPregnant()) {
                    giveBirth(wildlifeEntity);
                }
            }
        }
    }

    private void giveBirth(CoreAnimalEntity female) {
        if (!female.isTouchingWater()) {
            int offspring = (random.nextInt(female.getMaxOffspring() - female.getMinOffspring()) + female.getMinOffspring());
            if (female.rareOffspring()) {
                offspring = female.getMaxOffspring() - 1;
                if (random.nextInt(50) == 0) {
                    offspring = female.getMaxOffspring();
                }
            }
            for (int i = 0; i < offspring; i++) {
                female.getWorld().syncWorldEvent(2001, female.getSteppingPos(), Block.getRawIdFromState(female.getSteppingBlockState()));
                PassiveEntity baby = female.createChild((ServerWorld) female.getWorld(), female);
                if (baby instanceof CoreAnimalEntity wildlifeEntityBaby) {
                    if (wildlifeEntityBaby.getVariant().isEmpty()) {
                        wildlifeEntityBaby.setVariant(wildlifeEntityBaby.calculateInheritedVariant(female.getVariant(), female.getMateVariant()));
                    }
                    wildlifeEntityBaby.setAgeTicks(0);
                    wildlifeEntityBaby.setGender(this.random.nextInt(2));
                    if (!wildlifeEntityBaby.isMigratory()) {
                        if (Objects.equals(wildlifeEntityBaby.getWorld().getBlockState(wildlifeEntityBaby.getSteppingPos()).getBlock(), wildlifeEntityBaby.getHomeBlockType())) {
                            wildlifeEntityBaby.setHomePos(wildlifeEntityBaby.getSteppingPos());
                            wildlifeEntityBaby.setPositionTarget(wildlifeEntityBaby.getSteppingPos(), wildlifeEntityBaby.getMaxRoamDistance());
                        }
                    }
                    wildlifeEntityBaby.setMotherUUID(female.getUuidAsString());
                    wildlifeEntityBaby.setHunger(wildlifeEntityBaby.getMaxFood() / 2);
                    //wildlifeEntityBaby.setEnrichment(wildlifeEntityBaby.getMaxEnrichment());
                    wildlifeEntityBaby.setHungerTicks(1600);
                    //wildlifeEntityBaby.setEnrichmentTicks(2000);
                    /*if (wildlifeEntityBaby instanceof ZooTerrestrialEntity terrestrialBaby) {
                        terrestrialBaby.setTiredTicks(0);
                    }*/
                    //wildlifeEntityBaby.setNewBaby(true);
                    double offset = (0.1 * (random.nextInt(4) + 1));
                    wildlifeEntityBaby.refreshPositionAndAngles(female.getX() + offset, female.getY(), female.getZ() + offset, 0.0F, 0.0F);
                    if (female.getOwner() != null && wildlifeEntityBaby.canBeTamed()) {
                        wildlifeEntityBaby.tame((PlayerEntity) female.getOwner());
                    }
                    ((ServerWorld) female.getWorld()).spawnEntityAndPassengers(wildlifeEntityBaby);
                    female.getWorld().sendEntityStatus(female, (byte) 18);
                    if (getServerWorld(female).getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                        female.getWorld().spawnEntity(new ExperienceOrbEntity(female.getWorld(), female.getX(), female.getY(), female.getZ(), female.getRandom().nextInt(7) + 1));
                    }
                }
                female.setPregnancyTicks(0);
                female.setMotherTicks((int) ((female.getAdultDays() * 24000) * 0.6));
                /*if (female instanceof ZooTerrestrialEntity terrestrialFemale) {
                    terrestrialFemale.setTiredTicks(0);
                }*/
                if (getServerWorld(female).getEntity(UUID.fromString(female.getMateUUID())) instanceof CoreAnimalEntity male) {
                    if (male.isAlive()) {
                        if (!male.isMonogamous()) {
                            male.setMateUUID("");
                        }
                    }
                }
                if (!female.isMonogamous()) {
                    female.setMateUUID("");
                }
            }
        }
    }

    @Override
    protected boolean findTargetPos() {
        if (!wildlifeEntity.isMigratory()) {
            if (!Objects.equals(wildlifeEntity.getHomePos(), new BlockPos(BlockPos.ZERO))) {
                this.targetPos = wildlifeEntity.getHomePos();
            } else {
                this.targetPos = wildlifeEntity.getSteppingPos();
            }
        } else {
            this.targetPos = wildlifeEntity.getSteppingPos();
        }
        return true;
    }

    @Override
    protected boolean isTargetPos(@NotNull WorldView pLevel, @NotNull BlockPos pPos) {
        if (wildlifeEntity.isMigratory()) {
            return true;
        } else if (wildlifeEntity.getHomeBlockType() != null) {
            if (pLevel.getBlockState(pPos).getBlock().equals(wildlifeEntity.getHomeBlockType())) {
                homeBlock = pLevel.getBlockState(pPos).getBlock();
                return true;
            }
        }
        return false;
    }
}
