package com.collective.projectcore.blocks.enrichment;

import com.collective.projectcore.blocks.CoreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class CoreEnrichmentBlock extends CoreBlock {

    public CoreEnrichmentBlock(Settings settings) {
        super(settings);
    }

    public void playEnrichmentSound(World world, BlockPos pos) {}

    public void playEnrichmentParticles(World world, BlockPos pos) {}

    public void playEnrichmentSoundAndParticles(World world, BlockPos pos) {
        this.playEnrichmentSound(world, pos);
        this.playEnrichmentParticles(world, pos);
    }
}
