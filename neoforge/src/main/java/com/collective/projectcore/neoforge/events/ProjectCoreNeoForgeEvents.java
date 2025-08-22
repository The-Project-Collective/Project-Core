package com.collective.projectcore.neoforge.events;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.items.CompendiumItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@Mod(value = ProjectCore.MOD_ID)
public class ProjectCoreNeoForgeEvents {

    @SubscribeEvent
    public static void entityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        var item = event.getItemStack();
        if (item.getItem() instanceof CompendiumItem compendiumItem && event.getTarget() instanceof LivingEntity living && (living instanceof WolfEntity || living instanceof CatEntity)) {
            compendiumItem.useOnEntity(item, event.getEntity(), living, event.getHand());
        }
    }
}
