package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.blocks.enrichment.CoreEnrichmentBlock;
import com.collective.projectcore.entities.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CoreAnimalPlayWithEnrichmentGoal extends MoveToTargetPosGoal {

    Random random = new Random();
    protected CoreEnrichmentBlock enrichmentBlock;
    CoreAnimalEntity coreAnimalEntity;

    public CoreAnimalPlayWithEnrichmentGoal(CoreAnimalEntity coreAnimalEntity, double speed, int range) {
        super(coreAnimalEntity, speed, range);
        this.coreAnimalEntity = coreAnimalEntity;
    }

    @Override
    public boolean canStart() {
        if (this.coreAnimalEntity.isHappy() || this.coreAnimalEntity.isSleeping() || this.coreAnimalEntity.isResting() || this.coreAnimalEntity.isTired()) {
            return false;
        } else {
            return this.coreAnimalEntity.getEnrichmentCooldown() <= 0 && this.findTargetPos();
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.coreAnimalEntity.getEnrichment() >= this.coreAnimalEntity.getMaxEnrichment()  || this.coreAnimalEntity.isSleeping() || this.coreAnimalEntity.isResting() || this.coreAnimalEntity.isTired()) {
            return false;
        } else {
            return this.coreAnimalEntity.getEnrichmentCooldown() <= 0 && super.shouldContinue();
        }
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.hasReached()) {
            int enrichAmount = 5;
            this.coreAnimalEntity.setEnrichment(this.coreAnimalEntity.getEnrichment() + enrichAmount);
            if (this.coreAnimalEntity.getEnrichment() > this.coreAnimalEntity.getMaxEnrichment()) {
                this.coreAnimalEntity.setEnrichment(this.coreAnimalEntity.getMaxEnrichment());
            }

            this.coreAnimalEntity.getWorld().playSound(null, this.coreAnimalEntity.getSteppingPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, this.coreAnimalEntity.getPitch());
            this.coreAnimalEntity.setEnrichmentCooldown(this.random.nextInt(600) + 1000);
        }

    }

    @Override
    protected boolean isTargetPos(WorldView pLevel, @NotNull BlockPos pPos) {
        if (pLevel.getBlockState(pPos).getBlock() instanceof CoreEnrichmentBlock && pLevel.getBlockState(pPos).isIn(this.coreAnimalEntity.getAllowedEnrichment())) {
            this.enrichmentBlock = (CoreEnrichmentBlock)pLevel.getBlockState(pPos).getBlock();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return 2.0;
    }
}
