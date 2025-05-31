package com.collective.projectcore;

import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.CoreItems;

public final class ProjectCore {
    public static final String MOD_ID = "project_core";

    public static void init() {
        CoreBlocks.BLOCKS.register();
        CoreBlocks.BLOCK_ITEMS.register();
        CoreItems.ITEMS.register();
        CoreTabGroups.TAB_GROUPS.register();

    }
}
