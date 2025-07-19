package com.collective.projectcore.blocks.traps.net_traps;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blocks.traps.CoreNetTrapBlock;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SmallNetTrapBlock extends CoreNetTrapBlock {

    public static final MapCodec<SmallNetTrapBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(createSettingsCodec()).apply(instance, SmallNetTrapBlock::new));

    public SmallNetTrapBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return CoreBlockEntities.SMALL_NET_TRAP_ENTITY.get().instantiate(pos, state);
    }

}
