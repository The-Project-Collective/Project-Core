package com.collective.projectcore.blocks.traps;

import com.collective.projectcore.blockentities.traps.CoreTrapBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class CoreNetTrapBlock extends CoreTrapBlock implements Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public CoreNetTrapBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, true));
        BOX_COLLISION_OPEN = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
        BOX_COLLISION_SHUT = Block.createCuboidShape(0, 0, 0, 16, 16, 16);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        } else {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof CoreTrapBlockEntity blockEntity) {
                    blockEntity.tick(world, state, blockEntity);
                }
            };
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    if (checkPos.equals(pos)) continue;
                    BlockState checkState = world.getBlockState(checkPos);
                    FluidState fluid = checkState.getFluidState();
                    if (!fluid.isIn(FluidTags.WATER) || !checkState.isReplaceable()) {
                        return false;
                    }
                    if (checkState.getBlock() == this) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected boolean canReplace(BlockState state, ItemPlacementContext context) {
        return context.getWorld().getBlockState(context.getBlockPos()).getFluidState().isIn(FluidTags.WATER);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean waterlogged = fluidState.getFluid() == Fluids.WATER;
        return this.getDefaultState().with(WATERLOGGED, waterlogged);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.get(OPEN)) {
            return super.getCollisionShape(state, world, pos, context);
        }
        if (context instanceof EntityShapeContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity != null) {
                if (world.getBlockEntity(pos) instanceof CoreTrapBlockEntity trapBlockEntity && trapBlockEntity.isAllowed(entity)) {
                    return super.getCollisionShape(state, world, pos, context);
                } else {
                    return VoxelShapes.empty();
                }
            }
        }
        return VoxelShapes.empty();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }
}
