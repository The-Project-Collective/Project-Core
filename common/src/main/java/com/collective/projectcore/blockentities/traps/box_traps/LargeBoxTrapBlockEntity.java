package com.collective.projectcore.blockentities.traps.box_traps;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blockentities.traps.CoreTrapBlockEntity;
import com.collective.projectcore.groups.tags.CoreTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class LargeBoxTrapBlockEntity extends CoreTrapBlockEntity {

    public LargeBoxTrapBlockEntity(BlockPos pos, BlockState state) {
        super(CoreBlockEntities.LARGE_BOX_TRAP_ENTITY.get(), pos, state);
    }

    @Override
    public boolean isAllowed(Entity entity) {
        return entity.getType().isIn(CoreTags.LARGE_BOX_TRAP_WHITELIST);
    }

}
