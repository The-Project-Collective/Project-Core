package com.collective.projectcore.items;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.enrichment.ScratchingPostEnrichmentBlock;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.base.CoreBaseItem;
import com.collective.projectcore.items.entityitems.SnuffleLogEnrichmentItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage") // arch$tab seems to cause this?
public class CoreItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM);

    // === ENRICHMENT ==

    // Snuffle Logs
    public static final RegistrySupplier<Item> SNUFFLE_LOG_ACACIA = ITEMS.register("snuffle_log_acacia", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_acacia", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 0));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_BIRCH = ITEMS.register("snuffle_log_birch", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_birch", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 1));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_CHERRY = ITEMS.register("snuffle_log_cherry", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_cherry", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 2));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_CRIMSON = ITEMS.register("snuffle_log_crimson", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_crimson", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 3));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_DARK_OAK = ITEMS.register("snuffle_log_dark_oak", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_dark_oak", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 4));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_JUNGLE = ITEMS.register("snuffle_log_jungle", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_jungle", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 5));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_MANGROVE = ITEMS.register("snuffle_log_mangrove", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_mangrove", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 6));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_OAK = ITEMS.register("snuffle_log_oak", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_oak", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 7));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_PALE_OAK = ITEMS.register("snuffle_log_pale_oak", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_pale_oak", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 8));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_SPRUCE = ITEMS.register("snuffle_log_spruce", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_spruce", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 9));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_WARPED = ITEMS.register("snuffle_log_warped", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_warped", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.HUSBANDRY_ITEMS)), 10));


    // Floating Puzzle Feeder




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
