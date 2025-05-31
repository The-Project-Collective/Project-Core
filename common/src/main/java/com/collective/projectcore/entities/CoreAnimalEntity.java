package com.collective.projectcore.entities;

import com.collective.projectcore.groups.tags.CoreTags;
import com.collective.projectcore.items.CoreItems;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.EntityAttribute;
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
import net.minecraft.registry.entry.RegistryEntry;
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

import java.util.*;

public abstract class CoreAnimalEntity extends AnimalEntity implements Angerable, Tameable {

    private static final TrackedData<Float> AGE_SCALE;
    private static final TrackedData<Integer> AGE_TICKS;
    private static final TrackedData<Integer> ANGER_TIME;
    private static final TrackedData<Integer> BREEDING_TICKS;
    private static final TrackedData<Integer> ENRICHMENT;
    private static final TrackedData<Integer> ENRICHMENT_COOLDOWN;
    private static final TrackedData<Integer> ENRICHMENT_TICKS;
    private static final TrackedData<Integer> GENDER;
    private static final TrackedData<String> GENOME;
    private static final TrackedData<Integer> GROUP_SIZE;
    private static final TrackedData<BlockPos> HOME_POS;
    private static final TrackedData<Integer> HUNGER;
    private static final TrackedData<Integer> HUNGER_TICKS;
    private static final TrackedData<String> LEADER;
    private static final TrackedData<String> MATE_GENOME;
    private static final TrackedData<String> MATE_UUID;
    private static final TrackedData<String> MOTHER_UUID;
    private static final TrackedData<String> OFFSPRING;
    protected static final TrackedData<Optional<UUID>> OWNER_UUID;
    private static final TrackedData<String> PACK;
    private static final TrackedData<Integer> PREGNANCY_TICKS;
    protected static final TrackedData<Byte> TAMEABLE_FLAGS;

    private static final UniformIntProvider ANGER_TIME_RANGE;
    private UUID angryAt;

    protected boolean doesAge;
    protected boolean getsAngry;
    protected boolean doesBreed;
    protected boolean hasEnrichment;
    protected boolean hasGender;
    protected boolean hasHunger;
    protected boolean hasAPack;
    protected boolean canBeTamed;
    protected boolean hasGenetics;

    private boolean adultFlag = false;
    private boolean juviFlag = false;
    private boolean childFlag = false;

    private int counter = 0;

    protected CoreAnimalEntity(EntityType<? extends AnimalEntity> entityType, World world,
                               boolean doesAge, boolean getsAngry, boolean doesBreed, boolean hasEnrichment, boolean hasGender,
                               boolean hasHunger, boolean hasAPack, boolean canBeTamed, boolean hasGenetics) {
        super(entityType, world);
        this.doesAge = doesAge;
        this.getsAngry = getsAngry;
        this.doesBreed = doesBreed;
        this.hasEnrichment = hasEnrichment;
        this.hasGender = hasGender;
        this.hasHunger = hasHunger;
        this.hasAPack = hasAPack;
        this.canBeTamed = canBeTamed;
        this.hasGenetics = hasGenetics;
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

    public boolean hasEnrichment() {
        return hasEnrichment;
    }

    public boolean hasGender() {
        return hasGender;
    }

    public boolean hasHunger() {
        return hasHunger;
    }

    public boolean hasAPack() {
        return hasAPack;
    }

    public boolean canBeTamed() {
        return canBeTamed;
    }

    public boolean hasGenetics() {
        return hasGenetics;
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
                if (this.getGender() == 1) {
                    pregnancyHandler();
                }
            }
            if (this.hasHunger()) {
                hungerHandler();
            }
            if (this.hasAPack()) {
                packHandler();
            }
            if (this.hasEnrichment()) {
                enrichmentHandler();
            }
        }
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (counter < 20) {
            counter++;
        } else {
            calculateDimensions();
            counter = 0;
        }
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
        if (this.isAdult() && !this.isPregnant() && this.isFull() && this.isHappy()) {
            if (!this.isParent()) {
                if (this.getBreedingTicks() > 0) {
                    this.setBreedingTicks(this.getBreedingTicks() - 1);
                }
            }
        }
    }

    public void pregnancyHandler() {
        if (this.getPregnancyTicks() > 1) {
            this.setPregnancyTicks(this.getPregnancyTicks() - 1);
        }
    }

    // --- Enrichment Ticker ------------------------------------------------------------------------------------------
    public void enrichmentHandler() {
        if (this.getEnrichmentTicks() > 0) {
            int enrichmentLoss = 1;
            this.setEnrichmentTicks(this.getEnrichmentTicks() - enrichmentLoss);
        } else if (this.getEnrichmentTicks() <= 0) {
            if (this.getEnrichment() > 0) {
                this.setEnrichment(this.getEnrichment() - 1);
            }
            this.setEnrichmentTicks(this.random.nextInt(600) + 1000);
        }
        if (this.getEnrichmentCooldown() > 0) {
            int enrichmentCooldownLoss = 1;
            this.setEnrichmentCooldown(this.getEnrichmentCooldown() - enrichmentCooldownLoss);
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

    // --- Pack Ticker ------------------------------------------------------------------------------------------
    public void packHandler() {
        if (this.getPack().isEmpty()) {
            this.setPack(List.of(this.getUuidAsString()));
        } else {
            if (this.getPack().size() == 1) {
                if (this.getPack().getFirst().equals(" ") || this.getPack().getFirst().equals(".")) {
                    this.setPack(List.of(this.getUuidAsString()));
                }
            }
        }
    }



    // === MAIN METHODS =======================================================================================================================================================================

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (hand == Hand.MAIN_HAND && !player.getWorld().isClient()) {
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
            if (player.getMainHandStack().getItem().equals(CoreItems.DEV_TOOL.get())) {
                player.sendMessage(Text.literal("----------------------"), false);
                player.sendMessage(Text.literal("Gender: " + this.getGender()), false);
                player.sendMessage(Text.literal("Genome: " + this.getGenome()), false);
                player.sendMessage(Text.literal("Age: " + this.getAgeDays()), false);
                player.sendMessage(Text.literal("Health: " + this.getHealth()), false);
                player.sendMessage(Text.literal("UUID: " + this.getUuidAsString()), false);
                player.sendMessage(Text.literal("Mother UUID: " + this.getMotherUUID()), false);
                player.sendMessage(Text.literal("Mate UUID: " + this.getMateUUID()), false);
                player.sendMessage(Text.literal("Breeding Ticks: " + this.getBreedingTicks()), false);
                player.sendMessage(Text.literal("Pregnancy Ticks: " + this.getPregnancyTicks()), false);
                player.sendMessage(Text.literal("Parent?: " + this.isParent()), false);
                player.sendMessage(Text.literal("Offspring: " + this.getOffspring().size() + " | " + this.getOffspringString()), false);
                player.sendMessage(Text.literal("----------------------"), false);
            }
            if (player.getMainHandStack().getItem().equals(Items.STICK)) {
                this.setAgeTicks(this.getAgeTicks() + 24000);
                player.sendMessage(Text.literal("New Age: " + this.getAgeDays()), false);
            }
            if (player.getMainHandStack().getItem().equals(Items.REDSTONE)) {
                this.setBreedingTicks(0);
                player.sendMessage(Text.literal("New Breeding Ticks: " + this.getBreedingTicks()), false);
            }
            if (player.getMainHandStack().getItem().equals(Items.GLOWSTONE_DUST)) {
                this.setPregnancyTicks(1);
                player.sendMessage(Text.literal("New Pregnancy Ticks: " + this.getPregnancyTicks()), false);
            }
        }
        return super.interactMob(player, hand);
    }

    // --- Breeding ------------------------------------------------------------------------------------------
    @Override
    public boolean canBreedWith(AnimalEntity other) {
        if (other instanceof CoreAnimalEntity coreAnimalEntity) {
            if (this.getClass().equals(coreAnimalEntity.getClass())) {
                if (this.getGender() != coreAnimalEntity.getGender() && !this.isParent() && !coreAnimalEntity.isParent() && this.isAdult() && coreAnimalEntity.isAdult()) {
                    if (this.getBreedingTicks() <= 0 && coreAnimalEntity.getBreedingTicks() <= 0) {
                        if ((this.hasAPack() && this.getLeader().equals(coreAnimalEntity.getLeader())) || !this.hasAPack()) {
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
                if (this.getGender() == 1) {
                    this.setMateGenome(mate.getGenome());
                    this.setPregnancyTicks(this.getGestationTicks());
                    this.setBreedingTicks(this.random.nextInt(12000) + 12000);
                    mate.setBreedingTicks(this.random.nextInt(6000) + 6000);
                } else if (mate.getGender() == 1) {
                    mate.setMateGenome(this.getGenome());
                    mate.setPregnancyTicks(this.getGestationTicks());
                    mate.setBreedingTicks(this.random.nextInt(12000) + 12000);
                    this.setBreedingTicks(this.random.nextInt(6000) + 6000);
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
        } else {
            this.dataTracker.set(AGE_SCALE, this.getMinSize() + ((step * this.getAgeDays())));
        }
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

    public List<String> getOffspring() {
        String totalOffspring = this.getOffspringString();
        List<String> totalOffspringList = new ArrayList<>(List.of(totalOffspring.split("\\.")));
        List<String> toRemove = new ArrayList<>();
        for (String offspring : totalOffspringList) {
            if (!validateUUID(offspring)) {
                toRemove.add(offspring);
            }
        }
        if (!toRemove.isEmpty()) {
            totalOffspringList.removeAll(toRemove);
        }
        HashSet<String> finalOffspring = new HashSet<>(totalOffspringList);
        return finalOffspring.stream().toList();
    }

    public void setOffspring(List<String> newOffspring) {
        HashSet<String> newOffspringChecked = new HashSet<>(newOffspring);
        List<String> newOffspringList = newOffspringChecked.stream().toList();
        StringBuilder finalOffspring = new StringBuilder();
        for (String offspring : newOffspringList) {
            finalOffspring.append(".");
            finalOffspring.append(offspring);
        }
        this.setOffspringString(finalOffspring.toString());
    }

    public String getOffspringString() {
        return this.dataTracker.get(OFFSPRING);
    }

    public void setOffspringString(String offspring) {
        this.dataTracker.set(OFFSPRING, offspring);
    }

    public boolean isParent() {
        return !this.getOffspring().isEmpty();
    }

    // --- Gender ------------------------------------------------------------------------------------------
    public int getEnrichment() {
        return this.dataTracker.get(ENRICHMENT);
    }

    public void setEnrichment(int enrichment) {
        this.dataTracker.set(ENRICHMENT, enrichment);
    }

    public int getEnrichmentCooldown() {
        return this.dataTracker.get(ENRICHMENT_COOLDOWN);
    }

    public void setEnrichmentCooldown(int enrichmentCooldown) {
        this.dataTracker.set(ENRICHMENT_COOLDOWN, enrichmentCooldown);
    }

    public int getEnrichmentTicks() {
        return this.dataTracker.get(ENRICHMENT_TICKS);
    }

    public void setEnrichmentTicks(int enrichmentTicks) {
        this.dataTracker.set(ENRICHMENT_TICKS, enrichmentTicks);
    }

    public boolean isHappy() {
        return this.getEnrichment() >= this.getMaxEnrichment() * 0.8F;
    }

    public boolean isGrumpy() {
        return this.getEnrichment() < this.getMaxEnrichment() * 0.4F;
    }

    public boolean isAngry() {
        return this.getEnrichment() < this.getMaxEnrichment() * 0.2F;
    }

    // --- Gender ------------------------------------------------------------------------------------------
    public int getGender() {
        return this.dataTracker.get(GENDER);
    }

    public void setGender(int gender) {
        this.dataTracker.set(GENDER, gender);
    }

    // --- Genome ------------------------------------------------------------------------------------------
    public String getGenome() {
        return this.dataTracker.get(GENOME);
    }

    public void setGenome(String genome) {
        this.dataTracker.set(GENOME, genome);
    }

    public String getMateGenome() {
        return this.dataTracker.get(MATE_GENOME);
    }

    public void setMateGenome(String genome) {
        this.dataTracker.set(MATE_GENOME, genome);
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

    // --- Pack Mechanics ------------------------------------------------------------------------------------------
    public List<String> getPack() {
        String totalPack = this.getPackString();
        List<String> totalPackList = new ArrayList<>(List.of(totalPack.split("\\.")));
        List<String> toRemove = new ArrayList<>();
        for (String pack : totalPackList) {
            if (!validateUUID(pack)) {
                toRemove.add(pack);
            }
        }
        if (!toRemove.isEmpty()) {
            totalPackList.removeAll(toRemove);
        }
        HashSet<String> finalPack = new HashSet<>(totalPackList);
        return finalPack.stream().toList();
    }

    public void setPack(List<String> newPack) {
        HashSet<String> newPackChecked = new HashSet<>(newPack);
        List<String> newPackList = newPackChecked.stream().toList();
        StringBuilder finalPack = new StringBuilder();
        for (String packMember : newPackList) {
            finalPack.append(".");
            finalPack.append(packMember);
        }
        this.setPackString(finalPack.toString());
    }

    public String getPackString() {
        return this.dataTracker.get(PACK);
    }

    public void setPackString(String pack) {
        this.dataTracker.set(PACK, pack);
    }

    public boolean validateUUID(String name) {
        return !name.isEmpty() && !name.equals(" ") && !name.equals(".");
    }

    public String getLeader() {
        return this.dataTracker.get(LEADER);
    }

    public void setLeader(String isLeader) {
        this.dataTracker.set(LEADER, isLeader);
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
            if (this.hasGenetics() && this.getGenome() != null && !this.getGenome().isEmpty()) {
                return (float) this.calculateStats(this.getMaleMaxSize(), this.getGenome(), this.getSizeGeneIndex(), this.getSizeCoeff());
            } else {
                return this.getMaleMaxSize();
            }
        } else {
            if (this.hasGenetics() && this.getGenome() != null && !this.getGenome().isEmpty()) {
                return (float) this.calculateStats(this.getFemaleMaxSize(), this.getGenome(), this.getSizeGeneIndex(), this.getSizeCoeff());
            } else {
                return this.getFemaleMaxSize();
            }
        }
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        return super.getBaseDimensions(pose).scaled(this.getDimensionScaleWidth(), this.getDimensionScaleHeight());
    }

    public float getDimensionScaleHeight() {
        float step = (this.calculateMaxHeight() - this.getMinHeight()) / ((this.getAdultDays() * 24000) + 1);
        if (this.getAgeTicks() >= this.getAdultDays() * 24000) {
            return this.getMinHeight() + ((step) * this.getAdultDays() * 24000);
        }
        return getMinHeight() + (step * this.getAgeTicks());
    }

    public float getDimensionScaleWidth() {
        float step = (calculateMaxWidth() - this.getMinWidth()) / ((this.getAdultDays() * 24000) + 1);
        if (this.getAgeTicks() >= this.getAdultDays() * 24000) {
            return this.getMinWidth() + ((step) * this.getAdultDays() * 24000);
        }
        return getMinWidth() + (step * this.getAgeTicks());
    }

    public float calculateMaxHeight() {
        if (this.getGender() == 0) {
            return getMaxHeight();
        } else {
            return getMaxHeight() - getHeightDifference();
        }
    }

    public float calculateMaxWidth() {
        if (this.getGender() == 0) {
            return getMaxWidth();
        } else {
            return getMaxWidth() - getWidthDifference();
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

    public abstract boolean willParent();

    // --- Enrichment ------------------------------------------------------------------------------------------
    public abstract int getMaxEnrichment();

    // --- General ------------------------------------------------------------------------------------------
    @Override
    public abstract int getLimitPerChunk();

    // --- Genome ------------------------------------------------------------------------------------------
    public abstract String calculateGenome();

    public abstract String calculateInheritedGenome(String parent1, String parent2);

    public abstract String calculateWildGenome();

    public abstract boolean isGeneticallyViable(String genome);

    public abstract double calculateStats(double statValue, String genome, int geneIndex, float coefficient);

    public abstract int getSizeGeneIndex();

    public abstract float getSizeCoeff();

    public abstract int getAttributeGeneIndex(RegistryEntry<EntityAttribute> attribute);

    public abstract float getAttributeCoeff(RegistryEntry<EntityAttribute> attribute);

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

    // --- Pack Mechanics ------------------------------------------------------------------------------------------
    public abstract int getMaxGroupSize();

    // --- Pregnancy ------------------------------------------------------------------------------------------
    public abstract int getGestationTicks();

    // --- Roaming ------------------------------------------------------------------------------------------
    public abstract int getMaxRoamDistance();

    // --- Size ------------------------------------------------------------------------------------------
    public abstract float getMinSize();

    public abstract float getMaleMaxSize();

    public abstract float getFemaleMaxSize();

    public abstract float getMinHeight();

    public abstract float getMinWidth();

    public abstract float getMaxHeight();

    public abstract float getMaxWidth();

    public abstract float getHeightDifference();

    public abstract float getWidthDifference();


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

    

    // === DATA PROCESSING =======================================================================================================================================================================

    // --- Data Registry ------------------------------------------------------------------------------------------
    static {
        AGE_SCALE = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.FLOAT);
        AGE_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANGER_TIME = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
        BREEDING_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ENRICHMENT = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ENRICHMENT_COOLDOWN = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        ENRICHMENT_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        GENDER = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        GENOME = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        GROUP_SIZE = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HOME_POS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);
        HUNGER = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HUNGER_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);
        LEADER = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        MATE_GENOME = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        MATE_UUID = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        MOTHER_UUID = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        OFFSPRING = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        OWNER_UUID = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
        TAMEABLE_FLAGS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.BYTE);
        PACK = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.STRING);
        PREGNANCY_TICKS = DataTracker.registerData(CoreAnimalEntity.class, TrackedDataHandlerRegistry.INTEGER);

    }

    // --- Tracked Data ------------------------------------------------------------------------------------------
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(AGE_SCALE, 0f);
        builder.add(AGE_TICKS, 0);
        builder.add(ANGER_TIME, 0);
        builder.add(BREEDING_TICKS, 0);
        builder.add(ENRICHMENT, 0);
        builder.add(ENRICHMENT_COOLDOWN, 0);
        builder.add(ENRICHMENT_TICKS, 0);
        builder.add(GENDER, 0);
        builder.add(GENOME, "");
        builder.add(GROUP_SIZE, 0);
        builder.add(HOME_POS, new BlockPos(BlockPos.ZERO));
        builder.add(HUNGER, 0);
        builder.add(HUNGER_TICKS, 0);
        builder.add(LEADER, "");
        builder.add(MATE_GENOME, "");
        builder.add(MATE_UUID, "");
        builder.add(MOTHER_UUID, "");
        builder.add(OFFSPRING, "");
        builder.add(OWNER_UUID, Optional.empty());
        builder.add(PACK, "");
        builder.add(PREGNANCY_TICKS, 0);
        builder.add(TAMEABLE_FLAGS, (byte)0);

    }

    // --- NBT ------------------------------------------------------------------------------------------
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("AgeTicks", this.getAgeTicks());
        this.writeAngerToNbt(nbt);
        nbt.putInt("BreedingTicks", this.getBreedingTicks());
        nbt.putInt("Enrichment", this.getEnrichment());
        nbt.putInt("EnrichmentCooldown", this.getEnrichmentCooldown());
        nbt.putInt("EnrichmentTicks", this.getEnrichmentTicks());
        nbt.putInt("Gender", this.getGender());
        nbt.putString("Genome", this.getGenome());
        nbt.putInt("HomePosX", this.getHomePos().getX());
        nbt.putInt("HomePosY", this.getHomePos().getY());
        nbt.putInt("HomePosZ", this.getHomePos().getZ());
        nbt.putInt("Hunger", this.getHunger());
        nbt.putInt("HungerTicks", this.getHungerTicks());
        nbt.putString("Leader", this.getLeader());
        nbt.putString("MateUUID", this.getMateUUID());
        nbt.putString("MotherUUID", this.getMotherUUID());
        nbt.putString("Offspring", this.getOffspringString());
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        nbt.putString("Pack", this.getPackString());
        nbt.putInt("PregnancyTicks", this.getPregnancyTicks());

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setAgeTicks(nbt.getInt("AgeTicks"));
        this.readAngerFromNbt(this.getWorld(), nbt);
        this.setBreedingTicks(nbt.getInt("BreedingTicks"));
        this.setEnrichment(nbt.getInt("Enrichment"));
        this.setEnrichmentCooldown(nbt.getInt("EnrichmentCooldown"));
        this.setEnrichmentTicks(nbt.getInt("EnrichmentTicks"));
        this.setGender(nbt.getInt("Gender"));
        this.setGenome(nbt.getString("Genome"));
        this.setHomePos(new BlockPos(nbt.getInt("HomePosX"), nbt.getInt("HomePosY"), nbt.getInt("HomePosZ")));
        this.setHunger(nbt.getInt("Hunger"));
        this.setHungerTicks(nbt.getInt("HungerTicks"));
        this.setLeader(nbt.getString("Leader"));
        this.setMateUUID(nbt.getString("MateUUID"));
        this.setMotherUUID(nbt.getString("MotherUUID"));
        this.setOffspringString(nbt.getString("Offspring"));
        this.readTamingFromNBT(nbt);
        this.setPackString(nbt.getString("Pack"));
        this.setPregnancyTicks(nbt.getInt("PregnancyTicks"));

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
