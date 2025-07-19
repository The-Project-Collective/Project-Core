package com.collective.projectcore.blockentities.traps;

import com.collective.projectcore.blockentities.CoreBaseBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class CoreBoxTrapBlockEntity extends CoreBaseBlockEntity {

    private NbtCompound data;
    private boolean occupied;

    public CoreBoxTrapBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void captureEntity(MobEntity entity) {
        if (!this.isOccupied() && !Objects.requireNonNull(this.world).isClient()) {
            if (this.isAllowed(entity)) {
                NbtCompound compound = new NbtCompound();
                if (entity.saveNbt(compound)) {
                    if (!compound.contains("id")) {
                        compound.putString("id", entity.getType().toString());
                    }
                    this.setEntityData(compound);
                } else {
                    this.setEntityData(null);
                    System.out.println("Entity Data is null!!!");
                }
                System.out.println("Saving NBT: "+compound);
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

    private void setEntityData(@Nullable NbtCompound nbt) {
        this.data = nbt != null ? nbt.copy() : null;
        System.out.println("Data Check: "+this.data);
    }

    public boolean isOccupied() { return this.occupied; }

    private void setOccupied(boolean occupied) { this.occupied = occupied; }

    public void clearEntityData() {
        this.setEntityData(null);
        this.setOccupied(false);
        markDirty();
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        this.setEntityData(nbt.getCompound("EntityTag").copy());
        System.out.println("Loading Entity Data: "+nbt.getCompound("EntityTag").copy());
        this.setOccupied(nbt.getBoolean("Occupied"));
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (this.getEntityData() != null) {
            System.out.println("ID Present?: "+this.getEntityData().getString("id"));
            System.out.println("Writing Data Check: "+this.getEntityData());
            nbt.put("EntityTag", this.getEntityData().copy());
            System.out.println("Writing Entity Data: "+nbt.getCompound("EntityTag").copy());
        }
        nbt.putBoolean("Occupied", this.isOccupied());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

}
