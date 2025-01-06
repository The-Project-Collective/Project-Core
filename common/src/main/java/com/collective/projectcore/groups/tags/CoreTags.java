package com.collective.projectcore.groups.tags;

import com.collective.projectcore.ProjectCore;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class CoreTags {

    private static final String PRIMEVAL_ID = "project_wildlife";
    private static final String WILDLIFE_ID = "project_primeval";


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



    // === SPECIFIC DIETS ===

    // Wildlife
    public static final TagKey<Item> AMERICAN_RED_FOX = TagKey.of(RegistryKeys.ITEM, Identifier.of(WILDLIFE_ID, "diets/specific/american_red_fox"));

    // Primeval



    // === OTHER GROUPS ===

    // Items
    public static final TagKey<Item> INSECTS = TagKey.of(RegistryKeys.ITEM, Identifier.of(WILDLIFE_ID, "insects"));

}
