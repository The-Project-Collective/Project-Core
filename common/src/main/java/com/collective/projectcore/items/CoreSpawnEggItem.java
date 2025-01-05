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

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
                    baby.setAgeTicks(0);
                    baby.setGender(random.nextInt(2));
                    if (((CoreAnimalEntity) entity).isAdult()) {
                        baby.setMotherUUID(entity.getUuidAsString());
                    }
                    baby.setVariant(baby.calculateWildVariant());
                    baby.setAttributes(3);

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
