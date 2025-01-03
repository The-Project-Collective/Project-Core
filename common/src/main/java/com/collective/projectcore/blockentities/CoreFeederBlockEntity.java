package com.collective.projectcore.blockentities;

import com.collective.projectcore.blockentities.base.CoreBaseLockableContainerBlockEntity;
import com.collective.projectcore.entities.base.CoreBaseEntity;
import com.collective.projectcore.groups.tags.CoreTags;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CoreFeederBlockEntity extends CoreBaseLockableContainerBlockEntity implements SidedInventory, ExtendedMenuProvider {

    public static final int INVENTORY_SIZE = 18;
    private DefaultedList<ItemStack> inventory;

    public CoreFeederBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(null, blockPos, blockState);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    protected Text getContainerName() {
        return (Text) Text.EMPTY;
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    public void tick() {
        BlockState state = Objects.requireNonNull(this.world).getBlockState(this.pos);
        if (!this.isEmpty()) {
            this.world.setBlockState(this.pos, state.with(Properties.POWERED, true));
            markDirty();
        } else {
            this.world.setBlockState(this.pos, state.with(Properties.POWERED, false));
            markDirty();
        }
    }

    public boolean hasFood(CoreBaseEntity entity) {
        if (!this.isEmpty()) {
            for (ItemStack itemStack : this.inventory) {
                if (itemStack.isIn(entity.getGeneralDiet()) || itemStack.isIn(entity.getSpecificDiet())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void feedEntity(CoreBaseEntity entity) {
        for (ItemStack itemStack : this.inventory) {
            if (entity.getHunger() < entity.getMaxFood()) {
                if (itemStack.isIn(entity.getGeneralDiet()) || itemStack.isIn(entity.getSpecificDiet())) {
                    this.eatFood(entity, itemStack);
                }
            }
        }
    }

    public void eatFood(CoreBaseEntity entity, ItemStack stack) {
        boolean flag = false;
        boolean flag2 = true;
        while (flag2) {
            if (!stack.isEmpty()) {
                if (stack.isIn(entity.getGeneralDiet()) || stack.isIn(entity.getSpecificDiet())) {
                    if (entity.getHunger() < entity.getMaxFood()) {
                        entity.setHunger(entity.getHunger() + this.getFoodValue(entity, stack));
                        stack.decrement(1);
                        flag = true;
                    }
                    if (flag) {
                        entity.getWorld().playSound(null, entity.getSteppingPos(), SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.NEUTRAL, 1.0F, entity.getPitch());
                        if (entity.getWorld() != null) {
                            entity.getWorld().updateListeners(pos, getCachedState(), getCachedState(), 3);
                            markDirty();
                        }
                    } else {
                        flag2 = false;
                    }
                } else {
                    flag2 = false;
                }
            } else {
                flag2 = false;
            }
        }
    }

    public int getFoodValue(CoreBaseEntity entity, ItemStack stack) {
        int value = 0;
        if (stack.isIn(CoreTags.LARGE_FOODS)) {
            value = 6;
        } else if (stack.isIn(CoreTags.MEDIUM_FOODS)) {
            value = 4;
        } else if (stack.isIn(CoreTags.SMALL_FOODS)) {
            value = 2;
        }
        if (stack.isIn(entity.getSpecificDiet())) {
            value = value * 2;
        }
        return value;
    }

    @Override
    public void markDirty() {
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
        super.markDirty();
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return null;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public void saveExtraData(PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
}
