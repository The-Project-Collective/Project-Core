package com.collective.projectcore.blocks.traps.box_traps;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blocks.traps.CoreTrapBlock;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LargeBoxTrapBlock extends CoreTrapBlock {

    public static final MapCodec<LargeBoxTrapBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(createSettingsCodec()).apply(instance, LargeBoxTrapBlock::new));

    public LargeBoxTrapBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return CoreBlockEntities.LARGE_BOX_TRAP_ENTITY.get().instantiate(pos, state);
    }

}
