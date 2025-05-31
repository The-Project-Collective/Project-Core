package com.collective.projectcore.items;

import com.collective.projectcore.entities.CoreAnimalEntity;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class CoreSpawnEggItem extends ArchitecturySpawnEggItem {

    private final Random random = new Random();
    private final MutableText scientificName;

    public CoreSpawnEggItem(RegistrySupplier<? extends EntityType<? extends MobEntity>> entityType, MutableText scientific, Settings properties) {
        super(entityType, properties);
        this.scientificName = scientific;
    }

    @Override
    public Optional<MobEntity> spawnBaby(PlayerEntity user, MobEntity entity, EntityType<? extends MobEntity> entityType, ServerWorld world, Vec3d pos, ItemStack stack) {
        if (!this.isOfSameEntityType(world.getRegistryManager(), stack, entityType)) {
            return Optional.empty();
        } else {
            MobEntity mobEntity;
            if (entity instanceof CoreAnimalEntity parent) {
                if (parent.getGender() == 1) {
                    mobEntity = parent.createChild(world, parent);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }

            if (mobEntity == null) {
                return Optional.empty();
            } else {
                if (mobEntity instanceof CoreAnimalEntity baby) {
                    if (baby.doesAge()) {
                        baby.setAgeTicks(0);
                        baby.setAttributes(3);
                    } else {
                        baby.setAgeTicks(baby.getAdultDays() * 24000);
                        baby.setAttributes(0);
                    }
                    if (baby.doesBreed() && ((CoreAnimalEntity) entity).isAdult()) {
                        baby.setMotherUUID(entity.getUuidAsString());
                    }
                    if (baby.hasEnrichment()) {
                        baby.setEnrichment(baby.getMaxEnrichment());
                        baby.setEnrichmentTicks(random.nextInt(600) + 1000);
                    }
                    if (baby.hasGender()) {
                        baby.setGender(random.nextInt(2));
                    } else {
                        baby.setGender(2);
                    }
                    if (baby.hasHunger()) {
                        baby.setHunger(baby.getMaxFood() / 4);
                    }
                    if (baby.hasGenetics()) {
                        baby.setGenome(baby.calculateGenome());
                        if (baby.getGenome() != null && !baby.getGenome().isEmpty()) {
                            if (!baby.isGeneticallyViable(baby.getGenome())) {
                                return Optional.empty();
                            }
                        } else {
                            return Optional.empty();
                        }
                    }
                    if (baby.hasAPack()) {
                        List<String> motherPack = new ArrayList<>(((CoreAnimalEntity) entity).getPack());
                        motherPack.add(baby.getUuidAsString());
                        for (String packMember : motherPack) {
                            CoreAnimalEntity packMemberEntity = (CoreAnimalEntity) ((ServerWorld) entity.getWorld()).getEntity(UUID.fromString(packMember));
                            if (packMemberEntity != null) {
                                packMemberEntity.setPack(motherPack);
                            }
                        }
                    }
                    baby.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);
                    world.spawnEntityAndPassengers(baby);
                    baby.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));
                    stack.decrementUnlessCreative(1, user);
                    return Optional.of(baby);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(this.scientificName.formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
