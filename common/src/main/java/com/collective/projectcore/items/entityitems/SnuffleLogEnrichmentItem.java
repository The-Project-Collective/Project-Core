package com.collective.projectcore.items.entityitems;

import com.collective.projectcore.entities.CoreEntities;
import com.collective.projectcore.entities.enrichment.SnuffleLogEnrichmentEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class SnuffleLogEnrichmentItem extends Item {

    public int logType;

    public SnuffleLogEnrichmentItem(Item.Settings settings, int logType) {
        super(settings);
        this.logType = logType;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient()) {
            BlockHitResult hit = raycast(world, player, RaycastContext.FluidHandling.NONE);
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos spawnPos = hit.getBlockPos().offset(hit.getSide());
                SnuffleLogEnrichmentEntity entity = new SnuffleLogEnrichmentEntity(CoreEntities.SNUFFLE_LOG_ENTITY.get(), world);
                entity.setPosition(Vec3d.ofCenter(spawnPos));
                entity.setYaw(player.getYaw());
                entity.setEnrichmentType(logType);
                world.spawnEntity(entity);
                world.playSound(null, spawnPos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(Text.translatable("tooltip.project_core.enrichment_entity").formatted(Formatting.ITALIC, Formatting.GRAY));
    }
}
