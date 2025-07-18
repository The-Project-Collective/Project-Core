package com.collective.projectcore.blocks.traps;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blocks.CoreBoxTrapBlock;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class MediumBoxTrapBlock extends CoreBoxTrapBlock {

    public static final MapCodec<MediumBoxTrapBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(createSettingsCodec()).apply(instance, MediumBoxTrapBlock::new));

    public MediumBoxTrapBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return CoreBlockEntities.MEDIUM_BOX_TRAP_ENTITY.get().instantiate(pos, state);
    }

}
