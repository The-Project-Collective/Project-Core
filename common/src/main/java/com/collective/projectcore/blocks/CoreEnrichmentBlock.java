package com.collective.projectcore.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class CoreEnrichmentBlock extends CoreBlock {

    public static final EnumProperty<Direction> FACING = HorizontalFacingBlock.FACING;

    public CoreEnrichmentBlock(Settings settings) {
        super(settings);
    }
}
