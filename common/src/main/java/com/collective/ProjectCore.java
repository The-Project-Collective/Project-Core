package com.collective;

import com.collective.items.CoreItems;

public final class ProjectCore {
    public static final String MOD_ID = "project_core";

    public static void init() {
        CoreItems.ITEMS.register();
    }
}
