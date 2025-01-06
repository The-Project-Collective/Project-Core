package com.collective.projectcore.entities;

import com.collective.projectcore.entities.base.CoreAquaticAnimalEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.SwimAroundGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.function.Predicate;

public abstract class CoreFishEntity extends CoreAquaticAnimalEntity implements Bucketable {

    private static final TrackedData<Boolean> FROM_BUCKET;

    protected boolean canBeBucketed;

    protected CoreFishEntity(EntityType<? extends AnimalEntity> entityType, World world, boolean doesAge, boolean doesBreed, boolean hasGender, boolean hasHunger, boolean canBeTamed, boolean hasVariants, boolean canBeBucketed) {
        super(entityType, world, doesAge, doesBreed, hasGender, hasHunger, canBeTamed, hasVariants);
        this.moveControl = new CoreFishMoveControl(this);
        this.canBeBucketed = canBeBucketed;
    }

    // === CHARACTERISTICS CONTROL =======================================================================================================================================================================

    // --- Boolean Checks ------------------------------------------------------------------------------------------
    public boolean canBeBucketed() {
        return this.canBeBucketed;
    }



    // === Main Methods =======================================================================================================================================================================

    // --- Bucketable ------------------------------------------------------------------------------------------
    @Override
    @SuppressWarnings("deprecation")
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
    }

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.canBeBucketed()) {
            return Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
        } else {
            return super.interactMob(player, hand);
        }
    }

    // --- Goals ------------------------------------------------------------------------------------------
    @Override
    @SuppressWarnings("rawtypes")
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        Predicate<Entity> var10009 = EntityPredicates.EXCEPT_SPECTATOR;
        Objects.requireNonNull(var10009);
        //noinspection unchecked
        this.goalSelector.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 1.6, 1.4, var10009));
        this.goalSelector.add(4, new CoreSwimToRandomPlaceGoal(this));
    }

    // --- Movement ------------------------------------------------------------------------------------------
    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    public void travel(Vec3d movementInput) {
        if (this.isLogicalSideForUpdatingMovement() && this.isTouchingWater()) {
            this.updateVelocity(0.01F, movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0, -0.005, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    public void tickMovement() {
        if (!this.isTouchingWater() && this.isOnGround() && this.verticalCollision) {
            this.setVelocity(this.getVelocity().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F, 0.4000000059604645, (this.random.nextFloat() * 2.0F - 1.0F) * 0.05F));
            this.setOnGround(false);
            this.velocityDirty = true;
            this.playSound(this.getFlopSound());
        }
        super.tickMovement();
    }



    // === GETTERS & SETTERS =======================================================================================================================================================================

    // --- Aquatic ------------------------------------------------------------------------------------------
    protected boolean hasSelfControl() {
        return true;
    }

    protected abstract SoundEvent getFlopSound();

    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_FISH_SWIM;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    // --- Bucketable ------------------------------------------------------------------------------------------
    @Override
    public boolean isFromBucket() {
        return this.dataTracker.get(FROM_BUCKET);
    }

    @Override
    public void setFromBucket(boolean fromBucket) {
        this.dataTracker.set(FROM_BUCKET, fromBucket);
    }

    @Override
    public SoundEvent getBucketFillSound() {
        return SoundEvents.ITEM_BUCKET_FILL_FISH;
    }



    // === DATA PROCESSING =======================================================================================================================================================================

    // --- Data Registry ------------------------------------------------------------------------------------------
    static {
        FROM_BUCKET = DataTracker.registerData(CoreFishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    // --- Tracked Data ------------------------------------------------------------------------------------------
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(FROM_BUCKET, false);
    }

    // --- NBT ------------------------------------------------------------------------------------------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("FromBucket", this.isFromBucket());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setFromBucket(nbt.getBoolean("FromBucket"));
    }



    // === CUSTOM CLASSES =======================================================================================================================================================================

    // --- Move Control ------------------------------------------------------------------------------------------
    static class CoreFishMoveControl extends MoveControl {
        private final CoreFishEntity fish;

        CoreFishMoveControl(CoreFishEntity owner) {
            super(owner);
            this.fish = owner;
        }

        public void tick() {
            if (this.fish.isSubmergedIn(FluidTags.WATER)) {
                this.fish.setVelocity(this.fish.getVelocity().add(0.0, 0.005, 0.0));
            }

            if (this.state == State.MOVE_TO && !this.fish.getNavigation().isIdle()) {
                float f = (float)(this.speed * this.fish.getAttributeValue(EntityAttributes.MOVEMENT_SPEED));
                this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), f));
                double d = this.targetX - this.fish.getX();
                double e = this.targetY - this.fish.getY();
                double g = this.targetZ - this.fish.getZ();
                if (e != 0.0) {
                    double h = Math.sqrt(d * d + e * e + g * g);
                    this.fish.setVelocity(this.fish.getVelocity().add(0.0, (double)this.fish.getMovementSpeed() * (e / h) * 0.1, 0.0));
                }

                if (d != 0.0 || g != 0.0) {
                    float i = (float)(MathHelper.atan2(g, d) * 57.2957763671875) - 90.0F;
                    this.fish.setYaw(this.wrapDegrees(this.fish.getYaw(), i, 90.0F));
                    this.fish.bodyYaw = this.fish.getYaw();
                }

            } else {
                this.fish.setMovementSpeed(0.0F);
            }
        }
    }

    // --- Swimming Goal ------------------------------------------------------------------------------------------
    static class CoreSwimToRandomPlaceGoal extends SwimAroundGoal {
        private final CoreFishEntity fish;

        public CoreSwimToRandomPlaceGoal(CoreFishEntity fish) {
            super(fish, 1.0, 40);
            this.fish = fish;
        }

        public boolean canStart() {
            return this.fish.hasSelfControl() && super.canStart();
        }
    }

}
