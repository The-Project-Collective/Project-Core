package com.collective.projectcore;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.entities.CoreEntities;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.CoreItems;
import com.collective.projectcore.screens.handlers.CoreScreenHandlers;
import dev.architectury.platform.Platform;

public final class ProjectCore {

    public static final String MOD_ID = "project_core";

    private static final boolean wildlife_key = Platform.isModLoaded("project_wildlife");
    private static final boolean primeval_key = Platform.isModLoaded("project_primeval");

    public static void init() {
        CoreBlocks.BLOCKS.register();
        CoreBlocks.BLOCK_ITEMS.register();
        CoreBlockEntities.BLOCK_ENTITIES.register();
        CoreEntities.ENTITIES.register();
        CoreItems.ITEMS.register();
        CoreTabGroups.TAB_GROUPS.register();
        CoreScreenHandlers.SCREEN_HANDLERS.register();

    }

    public static boolean isWildlifeLoaded() {
        return wildlife_key;
    }

    public static boolean isPrimevalLoaded() {
        return primeval_key;
    }
}
