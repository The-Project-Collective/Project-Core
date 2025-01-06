package com.collective.projectcore.groups;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.items.CoreItems;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class CoreTabGroups {

    public static final DeferredRegister<ItemGroup> TAB_GROUPS = DeferredRegister.create(ProjectCore.MOD_ID, RegistryKeys.ITEM_GROUP);


    public static final RegistrySupplier<ItemGroup> CORE_BLOCKS = TAB_GROUPS.register("core_blocks", () -> CreativeTabRegistry
            .create(Text.translatable("group.project_core.core_blocks"), () -> new ItemStack(Blocks.DIAMOND_BLOCK)));
    public static final RegistrySupplier<ItemGroup> CORE_ITEMS = TAB_GROUPS.register("core_items", () -> CreativeTabRegistry
            .create(Text.translatable("group.project_core.core_items"), () -> new ItemStack(CoreItems.DEV_TOOL.get())));

}
