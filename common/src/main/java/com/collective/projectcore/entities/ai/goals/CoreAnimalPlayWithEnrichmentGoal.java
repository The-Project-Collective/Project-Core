package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.blocks.CoreEnrichmentBlock;
import com.collective.projectcore.blocks.enrichment.GnawingRockEnrichmentBlock;
import com.collective.projectcore.entities.CoreAnimalEntity;
import com.collective.projectcore.groups.tags.CoreTags;
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
        if (!coreAnimalEntity.isHappy()) {
            return false;
        } else if (coreAnimalEntity.getEnrichmentCooldown() > 0) {
            return false;
        } else {
            return this.findTargetPos();
        }
    }

    @Override
    public boolean shouldContinue() {
        if (coreAnimalEntity.getEnrichment() >= coreAnimalEntity.getMaxEnrichment()) {
            return false;
        } else if (coreAnimalEntity.getEnrichmentCooldown() > 0) {
            return false;
        } else {
            return super.shouldContinue();
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
        if (hasReached()) {
            int enrichAmount = 5;
            coreAnimalEntity.setEnrichment(coreAnimalEntity.getEnrichment() + enrichAmount);
            if (coreAnimalEntity.getEnrichment() > coreAnimalEntity.getMaxEnrichment()) {
                coreAnimalEntity.setEnrichment(coreAnimalEntity.getMaxEnrichment());
            }
            coreAnimalEntity.getWorld().playSound(null, coreAnimalEntity.getSteppingPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, coreAnimalEntity.getPitch());
            coreAnimalEntity.setEnrichmentCooldown(random.nextInt(600) + 1000);
        }
    }

    @Override
    protected boolean isTargetPos(WorldView pLevel, @NotNull BlockPos pPos) {
        if (pLevel.getBlockState(pPos).getBlock() instanceof CoreEnrichmentBlock && pLevel.getBlockState(pPos).isIn(coreAnimalEntity.getAllowedEnrichment())) {
            enrichmentBlock = (CoreEnrichmentBlock) pLevel.getBlockState(pPos).getBlock();
            return true;
        }
        return false;
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return 2.0D;
    }
}
