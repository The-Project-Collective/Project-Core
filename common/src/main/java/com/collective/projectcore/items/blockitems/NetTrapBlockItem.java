package com.collective.projectcore.items.blockitems;

import com.collective.projectcore.util.DirectItemPlacementContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class NetTrapBlockItem extends BlockItem {

    public NetTrapBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        Vec3d eyePos = player.getEyePos();
        Vec3d look = player.getRotationVec(1.0F);
        Vec3d targetVec = eyePos.add(look.normalize().multiply(2.0));
        BlockPos placePos = BlockPos.ofFloored(targetVec);
        Direction placementDirection = Direction.getFacing(look.x, look.y, look.z);
        if (!world.getFluidState(placePos).isIn(FluidTags.WATER)) {
            return ActionResult.FAIL;
        }
        if (!player.canPlaceOn(placePos, placementDirection, stack) || !world.canPlayerModifyAt(player, placePos)) {
            return ActionResult.FAIL;
        }
        DirectItemPlacementContext context = new DirectItemPlacementContext(
                world, placePos, placementDirection, stack, placementDirection
        );
        BlockState stateToPlace = this.getBlock().getPlacementState(context);
        if (stateToPlace == null || !stateToPlace.canPlaceAt(world, placePos)) {
            return ActionResult.FAIL;
        }
        if (!isSurroundedByWater(world, placePos, stateToPlace)) {
            return ActionResult.FAIL;
        }
        if (!world.isClient) {
            world.setBlockState(placePos, stateToPlace, Block.NOTIFY_ALL);
            stateToPlace.getBlock().onPlaced(
                    world,
                    placePos,
                    stateToPlace,
                    player,
                    stack
            );
            this.postPlacement(placePos, world, player, stack, stateToPlace);
            BlockSoundGroup sound = stateToPlace.getSoundGroup();
            world.playSound(null, placePos, sound.getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);

            if (!player.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        return ActionResult.SUCCESS;
    }

    public boolean isSurroundedByWater(WorldView world, BlockPos pos, BlockState stateToPlace) {
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
                    if (checkState.getBlock() == stateToPlace.getBlock()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        return state.canPlaceAt(context.getWorld(), context.getBlockPos());
    }
}
