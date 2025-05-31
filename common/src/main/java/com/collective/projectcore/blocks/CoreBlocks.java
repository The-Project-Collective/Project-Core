package com.collective.projectcore.blocks;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.enrichment.GnawingRockEnrichmentBlock;
import com.collective.projectcore.groups.CoreTabGroups;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class CoreBlocks {

    public static boolean husbandry = Platform.isModLoaded("project_wildlife") || Platform.isModLoaded("project_primeval");

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.BLOCK);
    public static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Block> GNAWING_ROCK_ANDESITE = registerBlock("gnawing_rock_andesite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.ANDESITE).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_andesite")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_BLACKSTONE = registerBlock("gnawing_rock_blackstone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.BLACKSTONE).mapColor(MapColor.BLACK).registryKey(getBlockRegistryKey("gnawing_rock_blackstone")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_CALCITE = registerBlock("gnawing_rock_calcite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.CALCITE).mapColor(MapColor.WHITE).registryKey(getBlockRegistryKey("gnawing_rock_calcite")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_COBBLED_DEEPSLATE = registerBlock("gnawing_rock_cobbled_deepslate", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.COBBLED_DEEPSLATE).mapColor(MapColor.DEEPSLATE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_cobbled_deepslate")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_COBBLESTONE = registerBlock("gnawing_rock_cobblestone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.COBBLESTONE).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_cobblestone")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_DEEPSLATE = registerBlock("gnawing_rock_deepslate", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DEEPSLATE).mapColor(MapColor.DEEPSLATE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_deepslate")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_DIORITE = registerBlock("gnawing_rock_diorite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DIORITE).mapColor(MapColor.WHITE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_diorite")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_DRIPSTONE = registerBlock("gnawing_rock_dripstone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.DRIPSTONE_BLOCK).mapColor(MapColor.DIRT_BROWN).registryKey(getBlockRegistryKey("gnawing_rock_dripstone")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_GRANITE = registerBlock("gnawing_rock_granite", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.GRANITE).mapColor(MapColor.DULL_RED).registryKey(getBlockRegistryKey("gnawing_rock_granite")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_SANDSTONE = registerBlock("gnawing_rock_sandstone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.SANDSTONE).mapColor(MapColor.PALE_YELLOW).registryKey(getBlockRegistryKey("gnawing_rock_sandstone")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_STONE = registerBlock("gnawing_rock_stone", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.STONE).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_stone")).nonOpaque()), "husbandry");
    public static final RegistrySupplier<Block> GNAWING_ROCK_TUFF = registerBlock("gnawing_rock_tuff", () -> new GnawingRockEnrichmentBlock(AbstractBlock.Settings.copy(Blocks.TUFF).mapColor(MapColor.STONE_GRAY).registryKey(getBlockRegistryKey("gnawing_rock_tuff")).nonOpaque()), "husbandry");


    // === HELPER METHODS ===

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block, String itemGroup) {
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        if (itemGroup.equals("core")) {
            registerCoreBlockItem(name, toReturn);
        } else if (itemGroup.equals("husbandry")) {
            registerHusbandryBlockItem(name, toReturn);
        }
        return toReturn;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static <T extends Block> void registerCoreBlockItem(String name, RegistrySupplier<T> block) {
        BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().arch$tab(CoreTabGroups.CORE_BLOCKS).registryKey(getItemRegistryKey(name)).useBlockPrefixedTranslationKey()));
    }

    @SuppressWarnings("UnstableApiUsage")
    private static <T extends Block> void registerHusbandryBlockItem(String name, RegistrySupplier<T> block) {
        BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().arch$tab(CoreTabGroups.HUSBANDRY_BLOCKS).registryKey(getItemRegistryKey(name)).useBlockPrefixedTranslationKey()));
        //BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Settings().arch$tab(husbandry ? CoreTabGroups.HUSBANDRY_BLOCKS : null).registryKey(getItemRegistryKey(name)).useBlockPrefixedTranslationKey()));
    }

    private static RegistryKey<Block> getBlockRegistryKey(String block_id) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, block_id));
    }

    private static RegistryKey<Item> getItemRegistryKey(String item_id) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, item_id));
    }
}
