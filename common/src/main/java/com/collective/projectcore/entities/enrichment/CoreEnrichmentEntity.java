package com.collective.projectcore.entities.enrichment;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class CoreEnrichmentEntity extends Entity {

    public static final TrackedData<Integer> ENRICHMENT_TYPE;
    public static final TrackedData<Float> ROLL_ANGLE;
    public static final TrackedData<Float> PREVIOUS_ROLL_ANGLE;

    private Vec3d previousVelocity = Vec3d.ZERO;


    public CoreEnrichmentEntity(EntityType<? extends CoreEnrichmentEntity> type, World world) {
        super(type, world);
        this.setNoGravity(false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            if (!this.hasNoGravity()) {
                Vec3d velocity = this.getVelocity();
                velocity = velocity.add(0.0, -0.04, 0.0); // Default gravity value
                this.setVelocity(velocity);
            }
            List<LivingEntity> nearbyEntities = this.getWorld().getNonSpectatingEntities(
                    LivingEntity.class,
                    this.getBoundingBox().expand(0.1)
            );
            for (LivingEntity entity : nearbyEntities) {
                if (entity == this.getVehicle() || entity.noClip) continue;
                Vec3d direction = this.getPos().subtract(entity.getPos()).normalize().multiply(0.1);
                this.addVelocity(direction.x, 0.0, direction.z);
            }
            Vec3d velocity = this.getVelocity().multiply(0.9, 1.0, 0.9);
            double maxSpeed = 0.3;
            if (velocity.lengthSquared() > maxSpeed * maxSpeed) {
                velocity = velocity.normalize().multiply(maxSpeed);
            }
            this.setVelocity(velocity);
            this.move(MovementType.SELF, velocity);
            this.setPreviousRollAngle(this.getRollAngle());
            double dx = velocity.x;
            double dz = velocity.z;
            double horizontalSpeed = Math.sqrt(dx * dx + dz * dz);
            if (horizontalSpeed > 0.001) {
                float radius = 0.25f;
                float deltaAngle = (float) ((horizontalSpeed / (2 * Math.PI * radius)) * 360f);
                double cross = (0 * dx) - (1 * dz);
                boolean isClockwise = cross < 0;
                this.setRollAngle(this.getRollAngle() + (isClockwise ? deltaAngle : -deltaAngle));
            }
        }
    }

    public void onPlayedWith(LivingEntity interactor) {
        Vec3d push = new Vec3d(
                (this.getX() - interactor.getX()) * 0.1,
                0.0,
                (this.getZ() - interactor.getZ()) * 0.1
        );
        this.addVelocity(push);
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, getX(), getY() + 0.5, getZ(), 5, 0.2, 0.2, 0.2, 0.0);
            this.getWorld().playSound(null, getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.NEUTRAL, 0.6F, 1.2F);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack heldStack = player.getMainHandStack();
        if (!player.getWorld().isClient()) {
            if (player.isSneaking() && heldStack.isEmpty()) {
                ItemStack enrichmentStack = new ItemStack(this.getItemForEnrichmentType(this.getEnrichmentType()));
                boolean added = player.getInventory().insertStack(enrichmentStack);
                if (!added) {
                    player.dropItem(enrichmentStack, false);
                }
                this.remove(RemovalReason.DISCARDED);
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(1f, 0.625f);
    }

    @Override
    public float getStepHeight() {
        return 1.0F;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public net.minecraft.network.packet.Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return super.createSpawnPacket(entityTrackerEntry);
    }

    public float getRollAngle() {
        return this.dataTracker.get(ROLL_ANGLE);
    }

    public void setRollAngle(float angle) {
        this.dataTracker.set(ROLL_ANGLE, angle);
    }

    public float getPreviousRollAngle() {
        return this.dataTracker.get(PREVIOUS_ROLL_ANGLE);
    }

    public void setPreviousRollAngle(float angle) {
        this.dataTracker.set(PREVIOUS_ROLL_ANGLE, angle);
    }

    public abstract Item getItemForEnrichmentType(int type);

    public int getEnrichmentType() {
        return this.dataTracker.get(ENRICHMENT_TYPE);
    }

    public void setEnrichmentType(int type) {
        this.dataTracker.set(ENRICHMENT_TYPE, type);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.getWorld().isClient()) return false;
        Entity attacker = source.getAttacker();
        if (attacker != null) {
            Vec3d direction = this.getPos().subtract(attacker.getPos()).normalize().multiply(0.5);
            this.addVelocity(direction.x, 0.1, direction.z);
        } else if (source.getSource() != null) {
            Vec3d direction = this.getPos().subtract(source.getSource().getPos()).normalize().multiply(0.4);
            this.addVelocity(direction.x, 0.1, direction.z);
        }
        return false;
    }

    static {
        ENRICHMENT_TYPE = DataTracker.registerData(CoreEnrichmentEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ROLL_ANGLE = DataTracker.registerData(CoreEnrichmentEntity.class, TrackedDataHandlerRegistry.FLOAT);
        PREVIOUS_ROLL_ANGLE = DataTracker.registerData(CoreEnrichmentEntity.class, TrackedDataHandlerRegistry.FLOAT);

    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(ENRICHMENT_TYPE, 0);
        builder.add(ROLL_ANGLE, 0f);
        builder.add(PREVIOUS_ROLL_ANGLE, 0f);

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        nbt.putInt("EnrichmentType", this.getEnrichmentType());
        nbt.putFloat("RollAngle", this.getRollAngle());
        nbt.putFloat("PreviousRollAngle", this.getPreviousRollAngle());

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        this.setEnrichmentType(nbt.getInt("EnrichmentType"));
        this.setRollAngle(nbt.getFloat("RollAngle"));
        this.setPreviousRollAngle(nbt.getFloat("PreviousRollAngle"));

    }

    public enum EnrichmentLogType {
        ACACIA(0, "acacia"),
        BIRCH(1, "birch"),
        CHERRY(2, "cherry"),
        CRIMSON(3, "crimson"),
        DARK_OAK(4, "dark_oak"),
        JUNGLE(5, "jungle"),
        MANGROVE(6, "mangrove"),
        OAK(7, "oak"),
        PALE_OAK(8, "pale_oak"),
        SPRUCE(9, "spruce"),
        WARPED(10, "warped");


        private final int id;
        private final String textureName;

        EnrichmentLogType(int id, String textureName) {
            this.id = id;
            this.textureName = textureName;
        }

        public int getId() { return id; }
        public String getTextureName() { return textureName; }

        public static EnrichmentLogType fromId(int id) {
            for (EnrichmentLogType type : values()) {
                if (type.id == id) return type;
            }
            return OAK;
        }
    }
}

