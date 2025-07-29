package com.collective.projectcore.blocks;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.enrichment.BubbleMachineEnrichmentBlock;
import com.collective.projectcore.blocks.enrichment.GnawingRockEnrichmentBlock;
import com.collective.projectcore.blocks.enrichment.ScratchingPostEnrichmentBlock;
import com.collective.projectcore.blocks.machines.FeederBlock;
import com.collective.projectcore.blocks.traps.box_traps.LargeBoxTrapBlock;
import com.collective.projectcore.blocks.traps.box_traps.MediumBoxTrapBlock;
import com.collective.projectcore.blocks.traps.box_traps.SmallBoxTrapBlock;
import com.collective.projectcore.blocks.traps.net_traps.LargeNetTrapBlock;
import com.collective.projectcore.blocks.traps.net_traps.MediumNetTrapBlock;
import com.collective.projectcore.blocks.traps.net_traps.SmallNetTrapBlock;
import com.collective.projectcore.groups.CoreTabGroups;
import com.collective.projectcore.items.blockitems.NetTrapBlockItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class CoreBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.BLOCK);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM);

    // === ENRICHMENT ===

    // --- Gnawing Rocks ---
    public static final RegistrySupplier<Block> GNAWING_ROCK_ANDESITE = registerBlock("gnawing_rock_andesite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.ANDESITE).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_andesite")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_BLACKSTONE = registerBlock("gnawing_rock_blackstone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.BLACKSTONE).mapColor(MapColor.BLACK).registryKey(getBlockRegistryKey("gnawing_rock_blackstone")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_CALCITE = registerBlock("gnawing_rock_calcite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.CALCITE).mapColor(MapColor.WHITE).registryKey(getBlockRegistryKey("gnawing_rock_calcite")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_COBBLED_DEEPSLATE = registerBlock("gnawing_rock_cobbled_deepslate", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE).mapColor(MapColor.DEEPSLATE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_cobbled_deepslate")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_COBBLESTONE = registerBlock("gnawing_rock_cobblestone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_cobblestone")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_DEEPSLATE = registerBlock("gnawing_rock_deepslate", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).mapColor(MapColor.DEEPSLATE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_deepslate")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_DIORITE = registerBlock("gnawing_rock_diorite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DIORITE).mapColor(MapColor.WHITE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_diorite")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_DRIPSTONE = registerBlock("gnawing_rock_dripstone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DRIPSTONE_BLOCK).mapColor(MapColor.DIRT_BROWN).registryKey(getBlockRegistryKey("gnawing_rock_dripstone")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_GRANITE = registerBlock("gnawing_rock_granite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.GRANITE).mapColor(MapColor.DULL_RED).registryKey(getBlockRegistryKey("gnawing_rock_granite")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_SANDSTONE = registerBlock("gnawing_rock_sandstone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.SANDSTONE).mapColor(MapColor.PALE_YELLOW).registryKey(getBlockRegistryKey("gnawing_rock_sandstone")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_STONE = registerBlock("gnawing_rock_stone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_stone")).nonOpaque().requiresTool().solid()));
    public static final RegistrySupplier<Block> GNAWING_ROCK_TUFF = registerBlock("gnawing_rock_tuff", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.TUFF).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_tuff")).nonOpaque().requiresTool().solid()));

    // --- Scratching Log ---
    public static final RegistrySupplier<Block> SCRATCHING_POST_ACACIA = registerBlock("scratching_post_acacia", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.ACACIA_LOG).mapColor(MapColor.ORANGE).registryKey(getBlockRegistryKey("scratching_post_acacia")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_BIRCH = registerBlock("scratching_post_birch", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.BIRCH_LOG).mapColor(MapColor.WHITE).registryKey(getBlockRegistryKey("scratching_post_birch")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_CHERRY = registerBlock("scratching_post_cherry", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.CHERRY_LOG).mapColor(MapColor.DARK_DULL_PINK).registryKey(getBlockRegistryKey("scratching_post_cherry")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_CRIMSON = registerBlock("scratching_post_crimson", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.CRIMSON_STEM).mapColor(MapColor.DARK_RED).registryKey(getBlockRegistryKey("scratching_post_crimson")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_DARK_OAK = registerBlock("scratching_post_dark_oak", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DARK_OAK_LOG).mapColor(MapColor.BROWN).registryKey(getBlockRegistryKey("scratching_post_dark_oak")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_JUNGLE = registerBlock("scratching_post_jungle", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.JUNGLE_LOG).mapColor(MapColor.BROWN).registryKey(getBlockRegistryKey("scratching_post_jungle")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_MANGROVE = registerBlock("scratching_post_mangrove", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.MANGROVE_LOG).mapColor(MapColor.RED).registryKey(getBlockRegistryKey("scratching_post_mangrove")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_OAK = registerBlock("scratching_post_oak", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG).mapColor(MapColor.BROWN).registryKey(getBlockRegistryKey("scratching_post_oak")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_PALE_OAK = registerBlock("scratching_post_pale_oak", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.PALE_OAK_LOG).mapColor(MapColor.GRAY).registryKey(getBlockRegistryKey("scratching_post_pale_oak")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_SPRUCE = registerBlock("scratching_post_spruce", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_LOG).mapColor(MapColor.SPRUCE_BROWN).registryKey(getBlockRegistryKey("scratching_post_spruce")).nonOpaque()));
    public static final RegistrySupplier<Block> SCRATCHING_POST_WARPED = registerBlock("scratching_post_warped", () -> new ScratchingPostEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.WARPED_STEM).mapColor(MapColor.BLUE).registryKey(getBlockRegistryKey("scratching_post_warped")).nonOpaque()));

    // --- Other Enrichment ---
    public static final RegistrySupplier<Block> BUBBLE_MACHINE = registerBlock("bubble_machine", () -> new BubbleMachineEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE).mapColor(MapColor.LIGHT_GRAY).registryKey(getBlockRegistryKey("bubble_machine")).nonOpaque()));



    // === TRAPS ===

    // Box Traps
    public static final RegistrySupplier<Block> LARGE_BOX_TRAP = registerBlock("large_box_trap", () -> new LargeBoxTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("large_box_trap")).nonOpaque()));
    public static final RegistrySupplier<Block> MEDIUM_BOX_TRAP = registerBlock("medium_box_trap", () -> new MediumBoxTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("medium_box_trap")).nonOpaque()));
    public static final RegistrySupplier<Block> SMALL_BOX_TRAP = registerBlock("small_box_trap", () -> new SmallBoxTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("small_box_trap")).nonOpaque()));

    // Net Traps
    public static final RegistrySupplier<Block> LARGE_NET_TRAP = registerNetTrapBlock("large_net_trap", () -> new LargeNetTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("large_net_trap")).nonOpaque()));
    public static final RegistrySupplier<Block> MEDIUM_NET_TRAP = registerNetTrapBlock("medium_net_trap", () -> new MediumNetTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("medium_net_trap")).nonOpaque()));
    public static final RegistrySupplier<Block> SMALL_NET_TRAP = registerNetTrapBlock("small_net_trap", () -> new SmallNetTrapBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("small_net_trap")).nonOpaque()));



    // === UTILITY / MACHINES ===

    public static final RegistrySupplier<Block> FEEDER = registerBlock("feeder", () -> new FeederBlock(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(MapColor.BROWN).requiresTool().registryKey(getBlockRegistryKey("feeder")).nonOpaque()));



    // === HELPER METHODS ===

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistrySupplier<T> registerNetTrapBlock(String name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        registerNetTrapBlockItem(name, toReturn);
        return toReturn;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static <T extends Block> void registerBlockItem(String name, RegistrySupplier<T> block) {
        BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().arch$tab(getHusbandryBlocksGroup()).registryKey(getItemRegistryKey(name)).useBlockPrefixedTranslationKey()));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static <T extends Block> void registerNetTrapBlockItem(String name, RegistrySupplier<T> block) {
        BLOCK_ITEMS.register(name, () -> new NetTrapBlockItem(block.get(), new Item.Settings().arch$tab(getHusbandryBlocksGroup()).registryKey(getItemRegistryKey(name)).useBlockPrefixedTranslationKey()));
    }

    public static RegistrySupplier<ItemGroup> getHusbandryBlocksGroup() {
        return (ProjectCore.isWildlifeLoaded() || ProjectCore.isPrimevalLoaded()) ? CoreTabGroups.PROJECT_BLOCKS : null;
    }
    private static RegistryKey<Block> getBlockRegistryKey(String block_id) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, block_id));
    }

    private static RegistryKey<Item> getItemRegistryKey(String item_id) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, item_id));
    }
}
