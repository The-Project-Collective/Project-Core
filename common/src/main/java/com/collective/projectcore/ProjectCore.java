package com.collective.projectcore;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.CoreItems;
import com.collective.projectcore.screens.handlers.CoreScreenHandlers;

public final class ProjectCore {
    public static final String MOD_ID = "project_core";

    public static void init() {
        CoreBlocks.BLOCKS.register();
        CoreBlocks.BLOCK_ITEMS.register();
        CoreBlockEntities.BLOCK_ENTITIES.register();
        CoreItems.ITEMS.register();
        CoreTabGroups.TAB_GROUPS.register();
        CoreScreenHandlers.SCREEN_HANDLERS.register();

    }
}
