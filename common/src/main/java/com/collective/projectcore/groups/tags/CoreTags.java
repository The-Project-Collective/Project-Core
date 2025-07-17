package com.collective.projectcore.groups.tags;

import com.collective.projectcore.ProjectCore;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CoreTags {

    private static final String PRIMEVAL_ID = "project_primeval";
    private static final String WILDLIFE_ID = "project_wildlife";

    // === DIETS ===

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



    // === ENRICHMENT ===

    // General
    public static final TagKey<Block> ALL_ENRICHMENT = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, "enrichment/all_enrichment"));

    // Specific Types
    public static final TagKey<Block> GNAWING_ROCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, "enrichment/gnawing_rocks"));
    public static final TagKey<Block> SCRATCHING_POSTS = TagKey.of(RegistryKeys.BLOCK, Identifier.of(ProjectCore.MOD_ID, "enrichment/scratching_posts"));



    // === TRAPS ===

    // Whitelists
    public static final TagKey<EntityType<?>> SMALL_BOX_TRAP_WHITELIST = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "traps/box_traps/small_box_trap_whitelist"));
    public static final TagKey<EntityType<?>> MEDIUM_BOX_TRAP_WHITELIST = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "traps/box_traps/medium_box_trap_whitelist"));
    public static final TagKey<EntityType<?>> LARGE_BOX_TRAP_WHITELIST = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "traps/box_traps/large_box_trap_whitelist"));



    // === ENTITY TYPES ===

    // General
    public static final TagKey<EntityType<?>> ALL_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "entity/all_entities"));

    // Specific Types
    public static final TagKey<EntityType<?>> AGEING_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "entity/ageing_entities"));
    public static final TagKey<EntityType<?>> GENDERED_ENTITIES = TagKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(ProjectCore.MOD_ID, "entity/gendered_entities"));



}
