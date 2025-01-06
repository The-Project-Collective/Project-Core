package com.collective.projectcore.items;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.base.CoreBaseItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage") // arch$tab seems to cause this?
public class CoreItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Item> DEV_TOOL = ITEMS.register("dev_tool", () -> new CoreBaseItem(getItemSettings("dev_tool", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.CORE_ITEMS))));


    // === HELPER METHODS ===

    public static Item.Settings getItemSettings(String id, Item.Settings settings) {
        return settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, id)));
    }
}
