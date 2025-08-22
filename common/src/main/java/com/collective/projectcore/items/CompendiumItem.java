package com.collective.projectcore.items;

import com.collective.projectcore.entities.CoreAnimalEntity;
import com.collective.projectcore.screens.handlers.CompendiumScreenHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class CompendiumItem extends Item {

    public CompendiumItem(Settings pProperties) {
        super(pProperties);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!user.getWorld().isClient && user instanceof ServerPlayerEntity serverPlayer && entity instanceof CoreAnimalEntity) {
            serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, playerInv, player) ->
                            new CompendiumScreenHandler(syncId, playerInv, entity),
                    Text.translatable("screen.project_core.creature_compendium.title")
            ));
        }
        return ActionResult.SUCCESS;
    }

}
