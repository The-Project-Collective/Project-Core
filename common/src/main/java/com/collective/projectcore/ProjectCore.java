package com.collective.projectcore;

import com.collective.projectcore.items.CoreItems;
import net.minecraft.util.ModStatus;

public final class ProjectCore {
    public static final String MOD_ID = "project_core";

    public static void init() {
        CoreItems.ITEMS.register();
    }




}
