package com.collective.projectcore.entities.base;

import com.collective.projectcore.groups.tags.CoreTags;
import com.collective.projectcore.items.CoreItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class CoreAnimalEntity extends AnimalEntity implements Angerable, Tameable {

    private static final TrackedData<Float> AGE_SCALE;
    private static final TrackedData<Integer> AGE_TICKS;
    private static final TrackedData<Integer> ANGER_TIME;
    private static final TrackedData<Integer> BREEDING_TICKS;
    private static final TrackedData<Integer> FATHER_TICKS;
    private static final TrackedData<Integer> GENDER;
    private static final TrackedData<BlockPos> HOME_POS;
    private static final TrackedData<Integer> HUNGER;
    private static final TrackedData<Integer> HUNGER_TICKS;
    private static final TrackedData<String> MATE_UUID;
    private static final TrackedData<String> MATE_VARIANT;
    private static final TrackedData<Integer> MOTHER_TICKS;
    private static final TrackedData<String> MOTHER_UUID;
    protected static final TrackedData<Optional<UUID>> OWNER_UUID;
    private static final TrackedData<Integer> PREGNANCY_TICKS;
    protected static final TrackedData<Byte> TAMEABLE_FLAGS;
    private static final TrackedData<String> VARIANT;

    private static final UniformIntProvider ANGER_TIME_RANGE;
    private UUID angryAt;

    protected boolean doesAge;
    protected boolean getsAngry;
    protected boolean doesBreed;
    protected boolean hasGender;
    protected boolean hasHunger;
    protected boolean canBeTamed;
    protected boolean hasVariants;

    private boolean adultFlag = false;
    private boolean juviFlag = false;
    private boolean childFlag = false;

    protected CoreAnimalEntity(EntityType<? extends AnimalEntity> entityType, World world,
                               boolean doesAge, boolean getsAngry, boolean doesBreed, boolean hasGender, boolean hasHunger, boolean canBeTamed, boolean hasVariants) {
        super(entityType, world);
        this.doesAge = doesAge;
        this.getsAngry = getsAngry;
        this.doesBreed = doesBreed;
        this.hasGender = hasGender;
        this.hasHunger = hasHunger;
        this.canBeTamed = canBeTamed;
        this.hasVariants = hasVariants;
    }

    // === CHARACTERISTICS CONTROL =======================================================================================================================================================================

    // --- Boolean Checks ------------------------------------------------------------------------------------------
    public boolean doesAge() {
        return doesAge;
    }

    public boolean getsAngry() {
        return getsAngry;
    }

    public boolean doesBreed() {
        return doesBreed;
    }

    public boolean hasGender() {
        return hasGender;
    }

    public boolean hasHunger() {
        return hasHunger;
    }

    public boolean canBeTamed() {
        return canBeTamed;
    }

    public boolean hasVariants() {
        return hasVariants;
    }

    // === TICK HANDLING =======================================================================================================================================================================

    // --- Main Tickers ------------------------------------------------------------------------------------------
    @Override
    protected void mobTick(ServerWorld world) {
        super.mobTick(world);
        if (!this.getWorld().isClient()) {
            if (this.doesAge()) {
                ageHandler();
            }
            if (this.doesBreed() && this.isAdult()) {
                breedingHandler();
                if (this.getGender() == 0) {
                    fatherHandler();
                } else {
                    motherHandler();
                    pregnancyHandler();
                }
            }
            if (this.hasHunger()) {
                hungerHandler();
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.getWorld().isClient && this.getsAngry()) {
            this.tickAngerLogic((ServerWorld)this.getWorld(), true);
        }
    }

    // --- Age Ticker ------------------------------------------------------------------------------------------
    public void ageHandler() {
        if (this.isAdult() && !adultFlag) {
            this.setAttributes(0);
            calculateDimensions();
            adultFlag = true;
        }
        if (this.isJuvenile() && !juviFlag) {
            this.setAttributes(1);
            calculateDimensions();
            juviFlag = true;
        }
        if (this.isChild() && !childFlag) {
            this.setAttributes(2);
            calculateDimensions();
            childFlag = true;
        }
        this.setAgeTicks(this.getAgeTicks() + 1);
        calculateAgeScale();
    }

    // --- Breeding Tickers ------------------------------------------------------------------------------------------
    public void breedingHandler() {
        if (this.isAdult() && !this.isPregnant() && this.isFull()) {
            if (!this.isBabyMother() && !this.isChildMother()) {
                if (this.getBreedingTicks() > 0) {
                    this.setBreedingTicks(this.getBreedingTicks() - 1);
                }
            }
        }
    }

    public void fatherHandler() {
        if (this.isFather()) {
            this.setFatherTicks(this.getFatherTicks() - 1);
        } else {
            this.setFatherTicks(0);
        }
    }

    public void motherHandler() {
        if (this.isBabyMother() || this.isChildMother()) {
            this.setMotherTicks(this.getMotherTicks() - 1);
        } else {
            this.setMotherTicks(0);
        }
    }

    public void pregnancyHandler() {
        if (this.getPregnancyTicks() > 1) {
            this.setPregnancyTicks(this.getPregnancyTicks() - 1);
        }
    }

    // --- Hunger Ticker ------------------------------------------------------------------------------------------
    public void hungerHandler() {
        if (this.getHungerTicks() > 0) {
            int hungerLoss = 1;
            this.setHungerTicks(this.getHungerTicks() - hungerLoss);
        } else if (this.getHungerTicks() <= 0) {
            if (this.getHunger() > 0) {
                this.setHunger(this.getHunger() - 1);
            } else if (this.getHunger() <= 0) {
                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    this.damage(serverWorld, this.getDamageSources().starve(), 1);
                    this.playHurtSound(this.getDamageSources().starve());
                }
            }
            this.setHungerTicks(this.random.nextInt(600) + 1000);
        }
    }



    // === MAIN METHODS =======================================================================================================================================================================

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.isValidFood(itemStack) && !this.hasAngerTime()) {
            if (this.getHunger() >= this.getMaxFood() && this.isFavouriteFood(itemStack)) {
                if (this.canBeTamed()) {
                    if (this.isTamed()) {
                        if (this.getHealth() < this.getMaxHealth()) {
                            this.handHeal(itemStack, player);
                            return ActionResult.SUCCESS;
                        }
                    } else if (!this.getWorld().isClient) {
                        itemStack.decrementUnlessCreative(1, player);
                        this.tryTame(player);
                        return ActionResult.SUCCESS_SERVER;
                    }
                } else {
                    if (this.getHealth() < this.getMaxHealth()) {
                        this.handHeal(itemStack, player);
                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                if (this.canEatNutritionally(itemStack)) {
                    this.handFeed(itemStack, player);
                }
            }
        }
        if (player.getMainHandStack().getItem().equals(CoreItems.DEV_TOOL)) {
            player.sendMessage(Text.literal("----------------------"), false);
            player.sendMessage(Text.literal("Gender: "+this.getGender()), false);
            player.sendMessage(Text.literal("Variant: "+this.getVariant()), false);
            player.sendMessage(Text.literal("Age: "+this.getAgeDays()), false);
            player.sendMessage(Text.literal("Health: "+this.getHealth()), false);
            player.sendMessage(Text.literal("UUID: "+this.getUuidAsString()), false);
            player.sendMessage(Text.literal("Mother UUID: "+this.getMotherUUID()), false);
            player.sendMessage(Text.literal("Mate UUID: "+this.getMateUUID()), false);
            player.sendMessage(Text.literal("Breeding Ticks: "+this.getBreedingTicks()), false);
            player.sendMessage(Text.literal("Pregnancy Ticks: "+this.getPregnancyTicks()), false);
            player.sendMessage(Text.literal("----------------------"), false);
        }
        if (player.getMainHandStack().getItem().equals(Items.STICK)) {
            this.setAgeTicks(this.getAgeTicks() + 24000);
            player.sendMessage(Text.literal("New Age: "+this.getAgeDays()), false);
        }
        if (player.getMainHandStack().getItem().equals(Items.REDSTONE)) {
            this.setBreedingTicks(0);
            player.sendMessage(Text.literal("New Breeding Ticks: "+this.getBreedingTicks()), false);
        }
        if (player.getMainHandStack().getItem().equals(Items.GLOWSTONE)) {
            this.setPregnancyTicks(1);
            player.sendMessage(Text.literal("New Pregnancy Ticks: "+this.getPregnancyTicks()), false);
        }
        return super.interactMob(player, hand);
    }

    // --- Breeding ------------------------------------------------------------------------------------------
    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other instanceof CoreAnimalEntity coreAnimalEntity) {
            if (this.getClass().equals(coreAnimalEntity.getClass())) {
                if (this.getGender() != coreAnimalEntity.getGender() && !this.isMother() && !this.isFather() && !coreAnimalEntity.isMother() && !coreAnimalEntity.isFather()) {
                    if (this.getBreedingTicks() <= 0 && coreAnimalEntity.getBreedingTicks() <= 0) {
                        if (this.getMateUUID().equals(coreAnimalEntity.getUuidAsString()) && coreAnimalEntity.getMateUUID().equals(this.getUuidAsString())) {
                            return !this.isPregnant() && !coreAnimalEntity.isPregnant();
                        } else if (!this.isMated() && !coreAnimalEntity.isMated()) {
                            return !this.isPregnant() && !coreAnimalEntity.isPregnant();
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void breed(ServerWorld world, AnimalEntity other) {
        if (other instanceof CoreAnimalEntity mate) {
            if (this.getMateUUID().isEmpty() && mate.getMateUUID().isEmpty()) {
                this.setMateUUID(mate.getUuidAsString());
                mate.setMateUUID(this.getUuidAsString());
            }
            if (this.getUuidAsString().equals(mate.getMateUUID())) {
                this.setMateVariant(mate.getVariant());
                mate.setMateVariant(this.getVariant());
                if (this.getGender() == 1) {
                    this.setPregnancyTicks(this.getGestationTicks());
                    this.setBreedingTicks(this.random.nextInt(12000) + 12000);
                    mate.setBreedingTicks(this.random.nextInt(6000) + 6000);
                    mate.setFatherTicks(this.getGestationTicks() + (int)((this.getAdultDays() * 24000) * 0.6));
                } else if (mate.getGender() == 1) {
                    mate.setPregnancyTicks(300);
                    mate.setBreedingTicks(this.random.nextInt(12000) + 12000);
                    this.setBreedingTicks(this.random.nextInt(6000) + 6000);
                    this.setFatherTicks(this.getGestationTicks() + (int)((this.getAdultDays() * 24000) * 0.6));
                }
            }
            world.sendEntityStatus(this, (byte)18);
        }
    }

    // --- Health ------------------------------------------------------------------------------------------
    public void handHeal(ItemStack itemStack, PlayerEntity player) {
        itemStack.decrementUnlessCreative(1, player);
        int amount = this.getFoodValue(itemStack);
        this.heal(amount);
        if (this.getHealth() > this.getMaxHealth()) {
            this.setHealth(this.getMaxHealth());
        }
    }

    // --- Hunger ------------------------------------------------------------------------------------------
    public void handFeed(ItemStack itemStack, PlayerEntity player) {
        itemStack.decrementUnlessCreative(1, player);
        int amount = this.getFoodValue(itemStack);
        this.setHunger(this.getHunger() + amount);
        int maxFood = this.getLowMaxFood();
        if (this.isFavouriteFood(itemStack)) {
            maxFood = this.getMaxFood();
        }
        if (this.getHunger() > maxFood) {
            this.setHunger(maxFood);
        }
    }

    // --- Taming ------------------------------------------------------------------------------------------
    private void tryTame(PlayerEntity player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
        } else {
            this.getWorld().sendEntityStatus(this, (byte)6);
        }
    }

    public void tame(PlayerEntity player) {
        this.setOwner(player);
        this.navigation.stop();
        this.setTarget(null);
        this.getWorld().sendEntityStatus(this, (byte)7);
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        World var3 = this.getWorld();
        if (var3 instanceof ServerWorld serverWorld) {
            if (serverWorld.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
                LivingEntity var4 = this.getOwner();
                if (var4 instanceof ServerPlayerEntity serverPlayerEntity) {
                    serverPlayerEntity.sendMessage(this.getDamageTracker().getDeathMessage());
                }
            }
        }
        super.onDeath(damageSource);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 7) {
            this.showEmoteParticle(true);
        } else if (status == 6) {
            this.showEmoteParticle(false);
        } else {
            super.handleStatus(status);
        }
    }

    protected void showEmoteParticle(boolean positive) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        if (!positive) {
            particleEffect = ParticleTypes.SMOKE;
        }
        for(int i = 0; i < 7; ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    // --- Unused ------------------------------------------------------------------------------------------
    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }



    // === GETTERS & SETTERS =======================================================================================================================================================================

    // --- Age ------------------------------------------------------------------------------------------
    public int getAgeTicks() {
        return this.dataTracker.get(AGE_TICKS);
    }

    public void setAgeTicks(int ticks) {
        this.dataTracker.set(AGE_TICKS, ticks);
    }

    public int getAgeDays() {
        return this.getAgeTicks() / 24000;
    }

    public void calculateAgeScale() {
        float maxSize = getGenderMaxSize();
        float step = (maxSize - this.getMinSize()) / (this.getAdultDays() + 1);
        if (this.getAgeTicks() >= this.getAdultDays() * 24000) {
            this.dataTracker.set(AGE_SCALE, this.getMinSize() + (step * this.getAdultDays()));
        }
        this.dataTracker.set(AGE_SCALE, this.getMinSize() + ((step * this.getAgeDays())));
    }

    public float getAgeScaleData() {
        return this.dataTracker.get(AGE_SCALE);
    }

    @Override
    public boolean isBaby() {
        return this.getAgeDays() <= (this.getAdultDays() * 0.3);
    }

    public boolean isChild() {
        return this.getAgeDays() <= 0.6 && this.getAgeDays() > 0.3;
    }

    public boolean isJuvenile() {
        return this.getAgeDays() < this.getAdultDays() && this.getAgeDays() > 0.6;
    }

    public boolean isAdult() {
        return this.getAgeDays() >= this.getAdultDays();
    }

    // --- Anger ------------------------------------------------------------------------------------------
    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.dataTracker.set(ANGER_TIME, angerTime);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    @Nullable
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.angryAt = angryAt;
    }

    // --- Attributes ------------------------------------------------------------------------------------------
    public void setAttributes(int age) {
        this.updateAttributes(age);
        this.setHealth((float) this.getAttributeBaseValue(EntityAttributes.MAX_HEALTH));
        if (this.getHomePos() != BlockPos.ZERO) {
            this.setPositionTarget(this.getHomePos(), this.getMaxRoamDistance());
        }
    }

    // --- Breeding ------------------------------------------------------------------------------------------
    public int getBreedingTicks() {
        return this.dataTracker.get(BREEDING_TICKS);
    }

    public void setBreedingTicks(int ticks) {
        this.dataTracker.set(BREEDING_TICKS, ticks);
    }

    public int getFatherTicks() {
        return this.dataTracker.get(FATHER_TICKS);
    }

    public void setFatherTicks(int ticks) {
        this.dataTracker.set(FATHER_TICKS, ticks);
    }

    public int getMotherTicks() {
        return this.dataTracker.get(MOTHER_TICKS);
    }

    public void setMotherTicks(int ticks) {
        this.dataTracker.set(MOTHER_TICKS, ticks);
    }

    public boolean isChildMother() {
        return this.getMotherTicks() > 0 && this.getMotherTicks() <= ((this.getAdultDays() * 24000) * 0.3);
    }

    public boolean isBabyMother() {
        return this.getMotherTicks() > ((this.getAdultDays() * 24000) * 0.3) && this.getMotherTicks() <= ((this.getAdultDays() * 24000) * 0.6);
    }

    public boolean isMother() {
        return this.getMotherTicks() > 0;
    }

    public boolean isFather() {
        return this.getFatherTicks() > 0;
    }
    

    // --- Gender ------------------------------------------------------------------------------------------
    public int getGender() {
        return this.dataTracker.get(GENDER);
    }

    public void setGender(int gender) {
        this.dataTracker.set(GENDER, gender);
    }

    // --- Home Pos ------------------------------------------------------------------------------------------
    public BlockPos getHomePos() {
        return this.dataTracker.get(HOME_POS);
    }

    public void setHomePos(BlockPos pHomePos) {
        this.dataTracker.set(HOME_POS, pHomePos);
    }


    // --- Hunger ------------------------------------------------------------------------------------------
    public int getHungerTicks() {
        return this.dataTracker.get(HUNGER_TICKS);
    }

    public void setHungerTicks(int hungerTicks) {
        this.dataTracker.set(HUNGER_TICKS, hungerTicks);
    }

    public int getHunger() {
        return this.dataTracker.get(HUNGER);
    }

    public void setHunger(int hunger) {
        this.dataTracker.set(HUNGER, hunger);
    }

    public int getLowMaxFood() {
        return Math.round(this.getMaxFood() * 0.8F);
    }

    public boolean isFull() {
        return this.getHunger() >= this.getMaxFood() * 0.8F;
    }

    public boolean isHungry() {
        return this.getHunger() < this.getMaxFood() * 0.4F;
    }

    public boolean isStarving() {
        return this.getHunger() < this.getMaxFood() * 0.2F;
    }

    public boolean isValidFood(ItemStack itemStack) {
        return itemStack.isIn(this.getGeneralDiet());
    }

    public boolean isFavouriteFood(ItemStack itemStack) {
        return itemStack.isIn(this.getSpecificDiet());
    }

    public int getFoodValue(ItemStack stack) {
        int value = 0;
        if (stack.isIn(CoreTags.LARGE_FOODS)) {
            value = 6;
        } else if (stack.isIn(CoreTags.MEDIUM_FOODS)) {
            value = 4;
        } else if (stack.isIn(CoreTags.SMALL_FOODS)) {
            value = 2;
        }
        if (stack.isIn(this.getSpecificDiet())) {
            value = value * 2;
        }
        return value;
    }

    public boolean canEatNutritionally(ItemStack itemStack) {
        if (this.isFull()) {
            return false;
        } else {
            if (this.isFavouriteFood(itemStack)) {
                return true;
            } else return this.isHungry() && this.isValidFood(itemStack);
        }
    }

    // --- Leash ------------------------------------------------------------------------------------------
    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.6F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
    }

    // --- Pregnancy ------------------------------------------------------------------------------------------
    public int getPregnancyTicks() {
        return this.dataTracker.get(PREGNANCY_TICKS);
    }

    public void setPregnancyTicks(int ticks) {
        this.dataTracker.set(PREGNANCY_TICKS, ticks);
    }

    public boolean isPregnant() {
        return this.getPregnancyTicks() > 0;
    }


    // --- Size ------------------------------------------------------------------------------------------
    public float getGenderMaxSize() {
        if (this.getGender() == 0 || this.getGender() == 2) {
            return this.getMaleMaxSize();
        } else {
            return this.getFemaleMaxSize();
        }
    }

    // --- Taming ------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public UUID getOwnerUuid() {
        return Optional.of(this.dataTracker.get(OWNER_UUID)).get().orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
        this.setOwnerUuid(player.getUuid());
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.TAME_ANIMAL.trigger(serverPlayerEntity, this);
        }
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE_FLAGS) & 4) != 0;
    }

    public void setTamed(boolean tamed) {
        byte b = this.dataTracker.get(TAMEABLE_FLAGS);
        if (tamed) {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 4));
        } else {
            this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -5));
        }
    }

    // --- UUIDs ------------------------------------------------------------------------------------------
    public String getMotherUUID() {
        return this.dataTracker.get(MOTHER_UUID);
    }

    public void setMotherUUID(String uuid) {
        this.dataTracker.set(MOTHER_UUID, uuid);
    }

    public String getMateUUID() {
        return this.dataTracker.get(MATE_UUID);
    }

    public void setMateUUID(String uuid) {
        this.dataTracker.set(MATE_UUID, uuid);
    }

    public boolean isMated() {
        return !this.getMateUUID().isEmpty();
    }


    // --- Variants ------------------------------------------------------------------------------------------
    public String getVariant() {
        return dataTracker.get(VARIANT);
    }

    public void setVariant(String variant) {
        dataTracker.set(VARIANT, variant);
    }

    public String getMateVariant() {
        return this.dataTracker.get(MATE_VARIANT);
    }

    public void setMateVariant(String variant) {
        this.dataTracker.set(MATE_VARIANT, variant);
    }





    // === OVERRIDES =======================================================================================================================================================================

    // --- Age ------------------------------------------------------------------------------------------
    public abstract int getAdultDays();

    // --- Attributes ------------------------------------------------------------------------------------------
    public abstract void updateAttributes(int age);

    // --- Breeding ------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public abstract PassiveEntity createChild(ServerWorld world, PassiveEntity entity);

    public abstract int getMaxOffspring();

    public abstract int getMinOffspring();

    public abstract boolean rareOffspring();

    public abstract boolean isMonogamous();

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public abstract int getLimitPerChunk();

    // --- Home Pos ------------------------------------------------------------------------------------------
    public abstract boolean isMigratory();

    public abstract Block getHomeBlockType();

    // --- Hunger ------------------------------------------------------------------------------------------
    public abstract int getMaxFood();

    public abstract TagKey<Item> getGeneralDiet();

    public abstract TagKey<Item> getSpecificDiet();

    // --- Leash ------------------------------------------------------------------------------------------
    @Override
    public abstract boolean canBeLeashed();

    // --- Pregnancy ------------------------------------------------------------------------------------------
    public abstract int getGestationTicks();

    // --- Roaming ------------------------------------------------------------------------------------------
    public abstract int getMaxRoamDistance();

    // --- Size ------------------------------------------------------------------------------------------
    public abstract float getMinSize();

    public abstract float getMaleMaxSize();

    public abstract float getFemaleMaxSize();

    // --- Sounds ------------------------------------------------------------------------------------------
    @Override
    protected abstract void playStepSound(BlockPos pos, BlockState state);

    @Override
    protected abstract SoundEvent getAmbientSound();

    @Override
    protected abstract SoundEvent getHurtSound(DamageSource source);

    @Override
    protected abstract SoundEvent getDeathSound();

    @Override
    protected abstract float getSoundVolume();

    // --- Variants ------------------------------------------------------------------------------------------
    public abstract String calculateInheritedVariant(String parent1, String parent2);

    public abstract String calculateWildVariant();

    

    // === DATA PROCESSING =======================================================================================================================================================================

    // --- Data Registry ------------------------------------------------------------------------------------------
    static {
        AGE_SCALE = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.FLOAT);
        AGE_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANGER_TIME = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
        BREEDING_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        FATHER_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        GENDER = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HOME_POS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        HUNGER = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HUNGER_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        MATE_UUID = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        MATE_VARIANT = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        MOTHER_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        MOTHER_UUID = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        OWNER_UUID = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        TAMEABLE_FLAGS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.BYTE);
        PREGNANCY_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        VARIANT = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);

    }

    // --- Tracked Data ------------------------------------------------------------------------------------------
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(AGE_SCALE, 0f);
        builder.add(AGE_TICKS, 0);
        builder.add(ANGER_TIME, 0);
        builder.add(BREEDING_TICKS, 0);
        builder.add(FATHER_TICKS, 0);
        builder.add(GENDER, 0);
        builder.add(HOME_POS, new BlockPos(BlockPos.ZERO));
        builder.add(HUNGER, 0);
        builder.add(HUNGER_TICKS, 0);
        builder.add(MATE_UUID, "");
        builder.add(MATE_VARIANT, "");
        builder.add(MOTHER_TICKS, 0);
        builder.add(MOTHER_UUID, "");
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(PREGNANCY_TICKS, 0);
        builder.add(TAMEABLE_FLAGS, (byte)0);
        builder.add(VARIANT, "");

    }

    // --- NBT ------------------------------------------------------------------------------------------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AgeTicks", this.getAgeTicks());
        this.writeAngerToNbt(nbt);
        nbt.putInt("BreedingTicks", this.getBreedingTicks());
        nbt.putInt("FatherTicks", this.getFatherTicks());
        nbt.putInt("Gender", this.getGender());
        nbt.putInt("HomePosX", this.getHomePos().getX());
        nbt.putInt("HomePosY", this.getHomePos().getY());
        nbt.putInt("HomePosZ", this.getHomePos().getZ());
        nbt.putInt("Hunger", this.getHunger());
        nbt.putInt("HungerTicks", this.getHungerTicks());
        nbt.putString("MateUUID", this.getMateUUID());
        nbt.putString("MateVariant", this.getMateVariant());
        nbt.putInt("MotherTicks", this.getMotherTicks());
        nbt.putString("MotherUUID", this.getMotherUUID());
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putInt("PregnancyTicks", this.getPregnancyTicks());
        nbt.putString("Variant", this.getVariant());

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAgeTicks(nbt.getInt("AgeTicks"));
        this.readAngerFromNbt(this.getWorld(), nbt);
        this.setBreedingTicks(nbt.getInt("BreedingTicks"));
        this.setFatherTicks(nbt.getInt("FatherTicks"));
        this.setGender(nbt.getInt("Gender"));
        this.setHomePos(new BlockPos(nbt.getInt("HomePosX"), nbt.getInt("HomePosY"), nbt.getInt("HomePosZ")));
        this.setHunger(nbt.getInt("Hunger"));
        this.setHungerTicks(nbt.getInt("HungerTicks"));
        this.setMateUUID(nbt.getString("MateUUID"));
        this.setMateVariant(nbt.getString("MateVariant"));
        this.setMotherTicks(nbt.getInt("MotherTicks"));
        this.setMotherUUID(nbt.getString("MotherUUID"));
        this.readTamingFromNBT(nbt);
        this.setPregnancyTicks(nbt.getInt("PregnancyTicks"));
        this.setVariant(nbt.getString("Variant"));

    }

    // --- Data Helper Methods ------------------------------------------------------------------------------------------
    public void readTamingFromNBT(NbtCompound nbt) {
        UUID uUID;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            try {
                this.setOwnerUuid(uUID);
                this.setTamed(true);
            } catch (Throwable var4) {
                this.setTamed(false);
            }
        } else {
            this.setOwnerUuid(null);
            this.setTamed(false);
        }
    }
}
