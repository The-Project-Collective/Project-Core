package com.collective.items;

import com.collective.ProjectCore;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage") // arch$tab seems to cause this?
public class CoreItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Item> DEV_TOOL = ITEMS.register("dev_tool", () -> new CoreBaseItem(getItemSettings("dev_tool", new Item.Settings().maxCount(64).arch$tab(ItemGroups.TOOLS))));


    // === HELPER METHODS ===

    public static Item.Settings getItemSettings(String id, Item.Settings settings) {
        return settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, id)));
    }
}
