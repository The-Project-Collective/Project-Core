package com.collective.projectcore.entities;

import com.collective.projectcore.groups.tags.CoreTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * This is the base animal entity class that all animals created by Project Core's dependent mods extends.
 */

public abstract class CoreAnimalEntity extends TameableEntity {

    private static final TrackedData<Float> AGE_SCALE;
    private static final TrackedData<Integer> AGE_TICKS;
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
    private static final TrackedData<Integer> PREGNANCY_TICKS;
    private static final TrackedData<String> VARIANT;

    private boolean adultFlag = false;
    private boolean juviFlag = false;
    private boolean childFlag = false;

    public CoreAnimalEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setTamed(false, false);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }
    

    // === TICK HANDLING =======================================================================================================================================================================

    // --- Main Ticker ------------------------------------------------------------------------------------------
    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient()) {
            ageTicker();
            if (this.isAdult()) {
                breedingHandler();
                if (this.getGender() == 0) {
                    fatherHandler();
                } else {
                    motherHandler();
                    pregnancyHandler();
                }
            }
            hungerHandler();
        }
    }

    // --- Age Ticker ------------------------------------------------------------------------------------------
    public void ageTicker() {
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
        if (this.isAdult() && !this.isPregnant()) {
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

    // === OTHER METHODS =======================================================================================================================================================================

    // --- Breeding ------------------------------------------------------------------------------------------
    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other instanceof CoreAnimalEntity coreAnimalEntity) {
            if (this.getClass().equals(coreAnimalEntity.getClass())) {
                if (this.getGender() != coreAnimalEntity.getGender()) {
                    if (!this.isMother() && !this.isFather() && !coreAnimalEntity.isMother() && !coreAnimalEntity.isFather()) {
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
                this.resetLoveTicks();
                mate.resetLoveTicks();
            }
            world.sendEntityStatus(this, (byte)18);
        }
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

    public boolean isMated() {
        return !this.getMateUUID().isEmpty();
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
        if (this.dataTracker.get(HUNGER) > this.getMaxFood()) {
            this.dataTracker.set(HUNGER, this.getMaxFood());
        }
    }

    public boolean isHungry() {
        return this.getHunger() < this.getMaxFood() * 0.8F;
    }

    public boolean isStarving() {
        return this.getHunger() < this.getMaxFood() * 0.2F;
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
        if (this.getGender() == 0) {
            return this.getMaleMaxSize();
        } else {
            return this.getFemaleMaxSize();
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
    public int getAdultDays() {
        return 1;
    }

    // --- Attributes ------------------------------------------------------------------------------------------
    public void updateAttributes(int age) { }

    // -- Breeding ------------------------------------------------------------------------------------------
    public boolean isMonogamous() {
        return false;
    }

    // --- Diet ------------------------------------------------------------------------------------------
    public int getMaxFood() {
        return 10;
    }

    public TagKey<Item> getGeneralDiet() {
        return CoreTags.ALL_FOODS;
    }

    public TagKey<Item> getSpecificDiet() {
        return CoreTags.ALL_FOODS;
    }

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public int getLimitPerChunk() {
        return 8;
    }

    // --- Home Pos ------------------------------------------------------------------------------------------
    public boolean isMigratory() {
        return false;
    }

    public Block getNestingBlock() {
        return null;
    }

    // --- Leash ------------------------------------------------------------------------------------------
    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public Vec3d getLeashOffset() {
        return new Vec3d(0.0, 0.6F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
    }

    // --- Offspring ------------------------------------------------------------------------------------------
    public int getMaxOffspring() {
        return 1;
    }

    public int getMinOffspring() {
        return 1;
    }

    public boolean rareOffspring() {
        return false;
    }

    // --- Pregnancy ------------------------------------------------------------------------------------------
    public int getGestationTicks() {
        return 120;
    }

    // --- Roaming ------------------------------------------------------------------------------------------
    public int getMaxRoamDistance() {
        return 1;
    }

    // --- Size ------------------------------------------------------------------------------------------
    public float getMinSize() {
        return 0.25f;
    }

    public float getMaleMaxSize() {
        return 1;
    }

    public float getFemaleMaxSize() {
        return 1;
    }

    // --- Sounds ------------------------------------------------------------------------------------------
    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WOLF_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    // --- Variants ------------------------------------------------------------------------------------------
    public String calculateInheritedVariant(String parent1, String parent2) {
        return "";
    }

    public String calculateWildVariant() {
        return "";
    }



    // === DATA PROCESSING =======================================================================================================================================================================

    // --- Data Registry ------------------------------------------------------------------------------------------
    static {
        AGE_SCALE = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.FLOAT);
        AGE_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
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
        PREGNANCY_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        VARIANT = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);

    }

    // --- Tracked Data ------------------------------------------------------------------------------------------
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(AGE_SCALE, 0f);
        builder.add(AGE_TICKS, 0);
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
        builder.add(PREGNANCY_TICKS, 0);
        builder.add(VARIANT, "");

    }

    // --- NBT ------------------------------------------------------------------------------------------
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AgeTicks", this.getAgeTicks());
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
        nbt.putInt("PregnancyTicks", this.getPregnancyTicks());
        nbt.putString("Variant", this.getVariant());

    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAgeTicks(nbt.getInt("AgeTicks"));
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
        this.setPregnancyTicks(nbt.getInt("PregnancyTicks"));
        this.setVariant(nbt.getString("Variant"));

    }
}
