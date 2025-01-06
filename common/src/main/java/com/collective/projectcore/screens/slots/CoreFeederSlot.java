package com.collective.projectcore.screens.slots;

import com.collective.projectcore.groups.tags.CoreTags;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CoreFeederSlot extends Slot {

    public CoreFeederSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isIn(CoreTags.ALL_FOODS);
    }
}
