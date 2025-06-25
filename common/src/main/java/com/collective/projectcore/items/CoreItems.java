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

    // === HUSBANDRY ===

    // Hormones
    public static final RegistrySupplier<Item> GROWTH_BOOSTING_HORMONE = ITEMS.register("growth_boosting_hormone", () -> new CoreBaseItem(getItemSettings("growth_boosting_hormone", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS))));
    public static final RegistrySupplier<Item> GROWTH_STUNTING_HORMONE = ITEMS.register("growth_stunting_hormone", () -> new CoreBaseItem(getItemSettings("growth_stunting_hormone", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS))));
    public static final RegistrySupplier<Item> FERTILITY_TREATMENT = ITEMS.register("fertility_treatment", () -> new CoreBaseItem(getItemSettings("fertility_treatment", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS))));
    public static final RegistrySupplier<Item> CONTRACEPTIVE_TREATMENT = ITEMS.register("contraceptive_treatment", () -> new CoreBaseItem(getItemSettings("contraceptive_treatment", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS))));



    // === MISCELLANEOUS ===

    public static final RegistrySupplier<Item> DEV_TOOL = ITEMS.register("dev_tool", () -> new CoreBaseItem(getItemSettings("dev_tool", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.CORE_ITEMS))));



    // === HELPER METHODS ===

    public static Item.Settings getItemSettings(String id, Item.Settings settings) {
        return settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, id)));
    }
}
