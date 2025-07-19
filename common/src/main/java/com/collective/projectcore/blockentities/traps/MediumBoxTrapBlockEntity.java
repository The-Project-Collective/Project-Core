package com.collective.projectcore.blockentities.traps;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.groups.tags.CoreTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class MediumBoxTrapBlockEntity extends CoreBoxTrapBlockEntity {

    public MediumBoxTrapBlockEntity(BlockPos pos, BlockState state) {
        super(CoreBlockEntities.MEDIUM_BOX_TRAP_ENTITY.get(), pos, state);
    }

    @Override
    public boolean isAllowed(Entity entity) {
        return entity.getType().isIn(CoreTags.MEDIUM_BOX_TRAP_WHITELIST);
    }

}
