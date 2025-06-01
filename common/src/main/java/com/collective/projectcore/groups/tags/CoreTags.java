package com.collective.projectcore.groups.tags;

import com.collective.projectcore.ProjectCore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CoreTags {

    private static final String PRIMEVAL_ID = "project_primeval";
    private static final String WILDLIFE_ID = "project_wildlife";

    // === GENERAL DIETS ===

    public static final TagKey<Block> ALL_ENRICHMENT = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, "enrichment/all_enrichment"));

    public static final TagKey<Block> GNAWING_ROCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, "enrichment/gnawing_rocks"));
    public static final TagKey<Block> SCRATCHING_POSTS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, "enrichment/scratching_posts"));



    // === GENERAL DIETS ===

    // Diets
    public static final TagKey<Item> ALL_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/general/all_foods"));

    public static final TagKey<Item> HERBIVORE_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/general/herbivore_foods"));
    public static final TagKey<Item> CARNIVORE_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/general/carnivore_foods"));
    public static final TagKey<Item> PISCIVORE_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/general/piscivore_foods"));
    public static final TagKey<Item> INSECTIVORE_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/general/insectivore_foods"));
    public static final TagKey<Item> OMNIVORE_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/general/omnivore_foods"));

    // Food Values
    public static final TagKey<Item> LARGE_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/values/large_foods.json"));
    public static final TagKey<Item> MEDIUM_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/values/medium_foods"));
    public static final TagKey<Item> SMALL_FOODS = TagKey.of(RegistryKeys.ITEM, Identifier.of(ProjectCore.MOD_ID, "diets/values/small_foods"));

}
