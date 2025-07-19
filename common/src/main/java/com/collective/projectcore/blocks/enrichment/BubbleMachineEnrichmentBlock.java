package com.collective.projectcore.blocks.enrichment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BubbleMachineEnrichmentBlock extends CoreEnrichmentBlock {

    public static final EnumProperty<Direction> FACING = Properties.FACING;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public BubbleMachineEnrichmentBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.withIfExists(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (!world.isClient) {
            boolean bl = state.get(POWERED);
            if (bl != world.isReceivingRedstonePower(pos)) {
                if (bl) {
                    world.scheduleBlockTick(pos, this, 4);
                } else {
                    world.setBlockState(pos, state.cycle(POWERED), 2);
                }
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, state.cycle(POWERED), 2);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            double d0 = pos.getX();
            double d1 = pos.getY();
            double d2 = pos.getZ();
            switch (state.get(FACING)) {
                case NORTH -> { d0 = d0 + 0.5; d1 = d1 + 0.5; }
                case EAST -> { d0 = d0 + 1; d1 = d1 + 0.5; d2 = d2 + 0.5; }
                case SOUTH -> { d0 = d0 + 0.5; d1 = d1 + 0.5; d2 = d2 + 1; }
                case WEST -> { d1 = d1 + 0.5; d2 = d2 + 0.5; }
                case UP -> { d0 = d0 + 0.5; d1 = d1 + 1; d2 = d2 + 0.5; }
                case DOWN -> { d0 = d0 + 0.5; d2 = d2 + 0.5; }
            }
            world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0, d1, d2, 0.0D, 0.04D, 0.0D);
            world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0, d1, d2, 0.0D, 0.04D, 0.0D);
            world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, d0, d1, d2, 0.0D, 0.04D, 0.0D);
            if (random.nextInt(200) == 0) {
                world.playSound(d0, d1, d2, SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        }
    }
}
