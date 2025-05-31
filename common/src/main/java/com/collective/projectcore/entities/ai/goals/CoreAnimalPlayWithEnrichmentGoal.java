package com.collective.projectcore.entities.ai.goals;

import com.collective.projectcore.blocks.CoreEnrichmentBlock;
import com.collective.projectcore.blocks.enrichment.GnawingRockEnrichmentBlock;
import com.collective.projectcore.entities.CoreAnimalEntity;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class CoreAnimalPlayWithEnrichmentGoal extends MoveToTargetPosGoal {

    Random random = new Random();
    protected CoreEnrichmentBlock enrichmentBlock;
    CoreAnimalEntity wildlifeEntity;
    List<String> preferredEnrichment;
    
    public CoreAnimalPlayWithEnrichmentGoal(CoreAnimalEntity wildlifeEntity, List<String> preferredEnrichment, double speed, int range) {
        super(wildlifeEntity, speed, range);
        this.wildlifeEntity = wildlifeEntity;
        this.preferredEnrichment = preferredEnrichment;
    }

    @Override
    public boolean canStart() {
        if (wildlifeEntity.getEnrichment() >= wildlifeEntity.getMaxEnrichment()) {
            return false;
        } else if (wildlifeEntity.getEnrichmentCooldown() > 0) {
            return false;
        } else {
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            } else {
                this.cooldown = this.getInterval(this.mob);
                return this.findTargetPos();
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        if (wildlifeEntity.getEnrichment() >= wildlifeEntity.getMaxEnrichment()) {
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
            int enrichAmount = 2;
            boolean isPreferred = enrichmentBlock instanceof GnawingRockEnrichmentBlock && preferredEnrichment.contains("gnawing_rock"); // Add future enrichment types to this list.
            if (isPreferred) {
                enrichAmount = 4;
            }
            wildlifeEntity.setEnrichment(wildlifeEntity.getEnrichment() + enrichAmount);
            wildlifeEntity.getWorld().playSound(null, wildlifeEntity.getSteppingPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, wildlifeEntity.getPitch());
            wildlifeEntity.setEnrichmentCooldown(random.nextInt(600) + 1000);
        }
    }

    @Override
    protected boolean isTargetPos(WorldView pLevel, @NotNull BlockPos pPos) {
        if (pLevel.getBlockState(pPos).getBlock() instanceof CoreEnrichmentBlock enrichBlock) {
            enrichmentBlock = enrichBlock;
            return true;
        }
        return false;
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return 2.0D;
    }
}
