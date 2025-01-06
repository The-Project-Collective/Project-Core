package com.collective.projectcore;

import com.collective.projectcore.items.CoreItems;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabOutput;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.utils.ArchitecturyConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.ModStatus;

public final class ProjectCore {
    public static final String MOD_ID = "project_core";

    public static void init() {
        CoreItems.ITEMS.register();
    }
}
