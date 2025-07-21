package com.collective.projectcore.blockentities.machines;

import com.collective.projectcore.blockentities.CoreBlockEntities;
import com.collective.projectcore.blockentities.CoreBaseLockableContainerBlockEntity;
import com.collective.projectcore.entities.CoreAnimalEntity;
import com.collective.projectcore.screens.handlers.machines.FeederScreenHandler;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FeederBlockEntity extends CoreBaseLockableContainerBlockEntity implements SidedInventory, ExtendedMenuProvider {

    public static final int INVENTORY_SIZE = 18;
    private DefaultedList<ItemStack> inventory;

    public FeederBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(CoreBlockEntities.FEEDER_ENTITY.get(), blockPos, blockState);
        this.inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("screenhandler.project_core.feeder");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FeederScreenHandler(syncId, playerInventory, this);
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

    public boolean hasFood(CoreAnimalEntity entity) {
        if (!this.isEmpty()) {
            for (ItemStack itemStack : this.inventory) {
                if (entity.isValidFood(itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void feedEntity(CoreAnimalEntity entity) {
        for (ItemStack itemStack : this.inventory) {
            int maxFood = entity.getLowMaxFood();
            if (entity.isFavouriteFood(itemStack)) {
                maxFood = entity.getMaxFood();
            }
            if (entity.getHunger() < maxFood && entity.isValidFood(itemStack)) {
                this.eatFood(entity, itemStack, maxFood);
            }
        }
    }

    public void eatFood(CoreAnimalEntity entity, ItemStack stack, int maxFood) {
        boolean flag;
        boolean flag2 = true;
        while (flag2) {
            if (!stack.isEmpty()) {
                if (entity.isValidFood(stack)) {
                    if (entity.getHunger() < maxFood) {
                        entity.setHunger(entity.getHunger() + entity.getFoodValue(stack));
                        if (entity.getHunger() > maxFood) {
                            entity.setHunger(maxFood);
                        }
                        flag = true;
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        if (entity.getWorld() instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), entity.getX(), entity.getY() + 1.0, entity.getZ(), 6, 0.15, 0.15, 0.15, 0.05);
                        }
                        entity.getWorld().playSound(null, entity.getSteppingPos(), SoundEvents.ENTITY_GENERIC_EAT.value(), SoundCategory.NEUTRAL, 1.0F, entity.getPitch());
                        stack.decrement(1);
                        if (entity.getWorld() != null) {
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
