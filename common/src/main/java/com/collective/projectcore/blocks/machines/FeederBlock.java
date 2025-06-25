package com.collective.projectcore.blocks.machines;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blockentities.FeederBlockEntity;
import com.collective.projectcore.blocks.CoreBlockWithEntity;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FeederBlock extends CoreBlockWithEntity {

    public static final MapCodec<FeederBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(createSettingsCodec()).apply(instance, FeederBlock::new));

    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public FeederBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return CoreBlockEntities.FEEDER_ENTITY.get().instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) {
            return null;
        } else {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof FeederBlockEntity blockEntity) {
                    blockEntity.tick();
                }
            };
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient() || !(player instanceof ServerPlayerEntity)) {
            return ActionResult.SUCCESS;
        }
        this.openMenu(state, world, pos, (ServerPlayerEntity) player);
        return ActionResult.CONSUME;
    }

    protected void openMenu(BlockState state, World world, BlockPos pos, ServerPlayerEntity player) {
        NamedScreenHandlerFactory factory = this.createScreenHandlerFactory(state, world, pos);
        if (factory != null) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ExtendedMenuProvider menuProvider) {
                MenuRegistry.openMenu(player, menuProvider);
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FeederBlockEntity feederEntity) {
                ItemScatterer.spawn(world, pos, feederEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, Properties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return Objects.requireNonNull(super.getPlacementState(ctx))
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(Properties.POWERED, false);
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
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
