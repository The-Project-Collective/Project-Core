package com.collective.projectcore.blocks.enrichment;

import com.collective.projectcore.blocks.CoreEnrichmentBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class GnawingRockEnrichmentBlock extends CoreEnrichmentBlock {

    private static final VoxelShape SHAPE = Block.createCuboidShape(1, 0, 1, 14, 6, 14);

    public GnawingRockEnrichmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
