package com.collective.projectcore.fabric.events;

import com.collective.projectcore.items.CompendiumItem;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.ActionResult;

public class ProjectCoreFabricEvents {

    public static void init() {
        entityInteract();
    }

    public static void entityInteract() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            var item = player.getMainHandStack();
            if (item.getItem() instanceof CompendiumItem compendiumItem && entity instanceof LivingEntity living && (living instanceof WolfEntity || living instanceof CatEntity)) {
                compendiumItem.useOnEntity(item, player, living, hand);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
