package com.collective.projectcore.util;

import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DirectItemPlacementContext extends ItemPlacementContext {

    public DirectItemPlacementContext(World world, BlockPos pos, Direction side, ItemStack stack, Direction playerFacing) {
        super(world, null, Hand.MAIN_HAND, stack, new BlockHitResult(
                Vec3d.ofCenter(pos),
                side,
                pos,
                false
        ));
    }
}