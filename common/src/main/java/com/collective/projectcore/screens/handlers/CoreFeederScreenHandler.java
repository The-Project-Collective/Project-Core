package com.collective.projectcore.screens.handlers;

import com.collective.projectcore.screens.handlers.base.CoreBaseScreenHandler;
import com.collective.projectcore.screens.slots.CoreFeederSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class CoreFeederScreenHandler extends CoreBaseScreenHandler {

    private final Inventory inventory;

    public CoreFeederScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(18));
    }

    public CoreFeederScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(null, syncId);
        checkSize(inventory, 18);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;

        // -- Feeder Inventory --
        for (m = 0; m < 2; ++m) {
            for (l = 0; l < 7; ++l) {
                this.addSlot(new CoreFeederSlot(inventory, l + m * 7, 26 + l * 18, 27 + m * 18));
            }
        }

        // -- Player Inventory --
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        // -- Player Hotbar --
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }
}
