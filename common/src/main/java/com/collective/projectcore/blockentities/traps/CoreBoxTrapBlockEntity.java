package com.collective.projectcore.blockentities.traps;

import com.collective.projectcore.blockentities.CoreBaseBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class CoreBoxTrapBlockEntity extends CoreBaseBlockEntity {

    private NbtCompound data;
    private boolean occupied;

    public CoreBoxTrapBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void captureEntity(MobEntity entity) {
        if (!this.isOccupied()) {
            if (this.isAllowed(entity)) {
                NbtCompound compound = new NbtCompound();
                this.setEntityData(entity.saveSelfNbt(compound) ? compound : null);
                this.setOccupied(true);
                entity.discard();
                markDirty();
            }
        }
    }

    public MobEntity releaseEntity(ServerWorld world) {
        if (!world.isClient() && this.isOccupied()) {
            if (this.getEntityData() != null) {
                Entity entity = EntityType.loadEntityWithPassengers(this.getEntityData(), world, SpawnReason.DISPENSER, (loadedEntity) -> loadedEntity);
                if (entity instanceof MobEntity mob) {
                    return mob;
                }
            }
        }
        return null;
    }

    public abstract boolean isAllowed(Entity entity);

    @Nullable
    public NbtCompound getEntityData() { return this.data; }

    private void setEntityData(@Nullable NbtCompound nbt) { this.data = nbt; }

    public boolean hasEntityData() { return this.data != null; }

    public boolean isOccupied() { return this.occupied; }

    private void setOccupied(boolean occupied) { this.occupied = occupied; }

    public void clearEntityData() {
        this.setEntityData(null);
        markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.setEntityData(nbt.copy());
        this.setOccupied(nbt.getBoolean("Occupied"));
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putBoolean("Occupied", this.isOccupied());
        if (this.getEntityData() != null) {
            nbt.put("SavedEntity", this.getEntityData().getCompound("SavedEntity"));
        }
    }

}
