package com.collective.projectcore.items;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.base.CoreBaseItem;
import com.collective.projectcore.items.entityitems.SnuffleLogEnrichmentItem;
import com.collective.projectcore.items.entityitems.ToyBallEnrichmentItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

@SuppressWarnings("UnstableApiUsage") // arch$tab seems to cause this?
public class CoreItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM);

    // === ENRICHMENT ==

    // Snuffle Logs
    public static final RegistrySupplier<Item> SNUFFLE_LOG_ACACIA = ITEMS.register("snuffle_log_acacia", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_acacia", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 0));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_BIRCH = ITEMS.register("snuffle_log_birch", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_birch", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 1));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_CHERRY = ITEMS.register("snuffle_log_cherry", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_cherry", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 2));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_CRIMSON = ITEMS.register("snuffle_log_crimson", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_crimson", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 3));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_DARK_OAK = ITEMS.register("snuffle_log_dark_oak", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_dark_oak", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 4));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_JUNGLE = ITEMS.register("snuffle_log_jungle", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_jungle", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 5));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_MANGROVE = ITEMS.register("snuffle_log_mangrove", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_mangrove", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 6));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_OAK = ITEMS.register("snuffle_log_oak", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_oak", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 7));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_PALE_OAK = ITEMS.register("snuffle_log_pale_oak", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_pale_oak", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 8));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_SPRUCE = ITEMS.register("snuffle_log_spruce", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_spruce", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 9));
    public static final RegistrySupplier<Item> SNUFFLE_LOG_WARPED = ITEMS.register("snuffle_log_warped", () -> new SnuffleLogEnrichmentItem(getItemSettings("snuffle_log_warped", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 10));

    // Toy Balls
    public static final RegistrySupplier<Item> TOY_BALL_WHITE = ITEMS.register("toy_ball_white", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_white", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 0));
    public static final RegistrySupplier<Item> TOY_BALL_LIGHT_GRAY = ITEMS.register("toy_ball_light_gray", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_light_gray", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 1));
    public static final RegistrySupplier<Item> TOY_BALL_GRAY = ITEMS.register("toy_ball_gray", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_gray", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 2));
    public static final RegistrySupplier<Item> TOY_BALL_BLACK = ITEMS.register("toy_ball_black", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_black", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 3));
    public static final RegistrySupplier<Item> TOY_BALL_BROWN = ITEMS.register("toy_ball_brown", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_brown", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 4));
    public static final RegistrySupplier<Item> TOY_BALL_RED = ITEMS.register("toy_ball_red", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_red", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 5));
    public static final RegistrySupplier<Item> TOY_BALL_ORANGE = ITEMS.register("toy_ball_orange", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_orange", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 6));
    public static final RegistrySupplier<Item> TOY_BALL_YELLOW = ITEMS.register("toy_ball_yellow", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_yellow", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 7));
    public static final RegistrySupplier<Item> TOY_BALL_LIME = ITEMS.register("toy_ball_lime", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_lime", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 8));
    public static final RegistrySupplier<Item> TOY_BALL_GREEN = ITEMS.register("toy_ball_green", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_green", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 9));
    public static final RegistrySupplier<Item> TOY_BALL_CYAN = ITEMS.register("toy_ball_cyan", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_cyan", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 10));
    public static final RegistrySupplier<Item> TOY_BALL_LIGHT_BLUE = ITEMS.register("toy_ball_light_blue", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_light_blue", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 11));
    public static final RegistrySupplier<Item> TOY_BALL_BLUE = ITEMS.register("toy_ball_blue", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_blue", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 12));
    public static final RegistrySupplier<Item> TOY_BALL_PURPLE = ITEMS.register("toy_ball_purple", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_purple", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 13));
    public static final RegistrySupplier<Item> TOY_BALL_MAGENTA = ITEMS.register("toy_ball_magenta", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_magenta", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 14));
    public static final RegistrySupplier<Item> TOY_BALL_PINK = ITEMS.register("toy_ball_pink", () -> new ToyBallEnrichmentItem(getItemSettings("toy_ball_pink", new Item.Settings().maxCount(64).arch$tab(getHusbandryBlocksGroup())), 15));




    // === HUSBANDRY ===

    // Hormones
    public static final RegistrySupplier<Item> GROWTH_BOOSTING_HORMONE = ITEMS.register("growth_boosting_hormone", () -> new CoreBaseItem(getItemSettings("growth_boosting_hormone", new Item.Settings().maxCount(64).arch$tab(getHusbandryItemsGroup()))));
    public static final RegistrySupplier<Item> GROWTH_STUNTING_HORMONE = ITEMS.register("growth_stunting_hormone", () -> new CoreBaseItem(getItemSettings("growth_stunting_hormone", new Item.Settings().maxCount(64).arch$tab(getHusbandryItemsGroup()))));
    public static final RegistrySupplier<Item> FERTILITY_TREATMENT = ITEMS.register("fertility_treatment", () -> new CoreBaseItem(getItemSettings("fertility_treatment", new Item.Settings().maxCount(64).arch$tab(getHusbandryItemsGroup()))));
    public static final RegistrySupplier<Item> CONTRACEPTIVE_TREATMENT = ITEMS.register("contraceptive_treatment", () -> new CoreBaseItem(getItemSettings("contraceptive_treatment", new Item.Settings().maxCount(64).arch$tab(getHusbandryItemsGroup()))));



    // === MISCELLANEOUS ===

    public static final RegistrySupplier<Item> DEV_TOOL = ITEMS.register("dev_tool", () -> new CoreBaseItem(getItemSettings("dev_tool", new Item.Settings().maxCount(64).arch$tab(CoreTabGroups.PROJECT_ITEMS))));



    // === HELPER METHODS ===

    public static RegistrySupplier<ItemGroup> getHusbandryBlocksGroup() {
        return (ProjectCore.isWildlifeLoaded() || ProjectCore.isPrimevalLoaded()) ? CoreTabGroups.PROJECT_BLOCKS : null;
    }

    public static RegistrySupplier<ItemGroup> getHusbandryItemsGroup() {
        return (ProjectCore.isWildlifeLoaded() || ProjectCore.isPrimevalLoaded()) ? CoreTabGroups.PROJECT_ITEMS : null;
    }

    public static Item.Settings getItemSettings(String id, Item.Settings settings) {
        return settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, id)));
    }
}
