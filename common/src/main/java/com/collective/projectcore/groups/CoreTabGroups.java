package com.collective.projectcore.groups;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.blocks.CoreBlocks;
import com.collective.projectcore.items.CoreItems;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class CoreTabGroups {

    public static final DeferredRegister<ItemGroup> TAB_GROUPS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM_GROUP);


    public static final RegistrySupplier<ItemGroup> PROJECT_BLOCKS = TAB_GROUPS.register("project_blocks", () -> CreativeTabRegistry
            .create(Text.translatable("group.project_core.project_blocks"), () -> new ItemStack(CoreBlocks.FEEDER.get())));
    public static final RegistrySupplier<ItemGroup> PROJECT_ITEMS = TAB_GROUPS.register("project_items", () -> CreativeTabRegistry
            .create(Text.translatable("group.project_core.project_items"), () -> new ItemStack(CoreItems.GROWTH_BOOSTING_HORMONE.get())));
    public static final RegistrySupplier<ItemGroup> PROJECT_ENTITIES = TAB_GROUPS.register("project_entities", () -> CreativeTabRegistry
            .create(Text.translatable("group.project_core.project_entities"), () -> new ItemStack(Items.ARMADILLO_SPAWN_EGG)));

}
