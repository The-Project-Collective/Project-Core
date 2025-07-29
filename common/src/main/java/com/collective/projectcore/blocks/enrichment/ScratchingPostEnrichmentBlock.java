package com.collective.projectcore.blocks.enrichment;

import com.collective.projectcore.items.CoreItems;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class ScratchingPostEnrichmentBlock extends CoreEnrichmentBlock {

    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 16, 14);
    public static final EnumProperty<DyeColor> COLOUR = EnumProperty.of("colour", DyeColor.class);
    public static final BooleanProperty UPPER = BooleanProperty.of("upper");

    public ScratchingPostEnrichmentBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(COLOUR, DyeColor.WHITE).with(UPPER, false));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOUR, UPPER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandStack();
        if (state.get(UPPER) && stack.getItem() instanceof DyeItem dyeItem) {
            DyeColor dye = dyeItem.getColor();
            if (state.get(COLOUR) != dye) {
                world.setBlockState(pos, state.with(COLOUR, dye), Block.NOTIFY_ALL);
                player.getWorld().playSoundFromEntity(player, player, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockPos up = pos.up();
        if (world.getBlockState(up).isReplaceable()) {
            world.setBlockState(pos, this.getDefaultState().with(UPPER, false), 3);
            world.setBlockState(up, this.getDefaultState().with(UPPER, true), 3);
        } else {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos otherPos = state.get(UPPER) ? pos.down() : pos.up();
        if (!world.isClient()) {
            BlockState otherState = world.getBlockState(otherPos);
            if (otherState.isOf(this) && otherState.get(UPPER) != state.get(UPPER)) {
                world.breakBlock(otherPos, !player.isCreative(), player);
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(UPPER)) {
            return world.getBlockState(pos.down()).isOf(this)
                    && !world.getBlockState(pos.down()).get(UPPER);
        } else {
            return world.getBlockState(pos.up()).isReplaceable();
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (pos.getY() < world.getHeight() - 1 && world.getBlockState(pos.up()).canReplace(ctx)) {
            return this.getDefaultState().with(UPPER, false);
        }
        return null;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void playEnrichmentSoundAndParticles(World world, BlockPos pos) {
        world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(this.getWoolTypeState(world, pos)));
    }

    public BlockState getWoolTypeState(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return switch (blockState.get(COLOUR)) {
            case WHITE -> Blocks.WHITE_WOOL.getDefaultState();
            case ORANGE -> Blocks.ORANGE_WOOL.getDefaultState();
            case MAGENTA -> Blocks.MAGENTA_WOOL.getDefaultState();
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL.getDefaultState();
            case YELLOW -> Blocks.YELLOW_WOOL.getDefaultState();
            case LIME -> Blocks.LIME_WOOL.getDefaultState();
            case PINK -> Blocks.PINK_WOOL.getDefaultState();
            case GRAY -> Blocks.GRAY_WOOL.getDefaultState();
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL.getDefaultState();
            case CYAN -> Blocks.CYAN_WOOL.getDefaultState();
            case PURPLE -> Blocks.PURPLE_WOOL.getDefaultState();
            case BLUE -> Blocks.BLUE_WOOL.getDefaultState();
            case BROWN -> Blocks.BROWN_WOOL.getDefaultState();
            case GREEN -> Blocks.GREEN_WOOL.getDefaultState();
            case RED -> Blocks.RED_WOOL.getDefaultState();
            case BLACK -> Blocks.BLACK_WOOL.getDefaultState();
        };
    }
}
