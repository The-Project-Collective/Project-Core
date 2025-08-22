package com.collective.projectcore.screens.handlers;

import com.collective.projectcore.screens.handlers.base.CoreBaseScreenHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;

public class CompendiumScreenHandler extends CoreBaseScreenHandler {

    private final LivingEntity target;
    private final PlayerInventory playerInventory;
    private final PropertyDelegate properties;

    private static final int ENTITY_ID = 0;

    public CompendiumScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    public CompendiumScreenHandler(int syncId, PlayerInventory playerInv, LivingEntity target) {
        super(CoreScreenHandlers.COMPENDIUM_SCREEN_HANDLER.get(), syncId);
        this.target = target;
        this.playerInventory = playerInv;
        this.properties = new ArrayPropertyDelegate(1);
        addProperties(this.properties);
        if (target != null) {
            properties.set(ENTITY_ID, target.getId());
        }
    }

    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if (target instanceof LivingEntity entity) {
            properties.set(ENTITY_ID, entity.getId());
        }
    }

    public LivingEntity getEntity() {
        if (playerInventory.player.getWorld() != null) {
            int id = properties.get(ENTITY_ID);
            Entity e = playerInventory.player.getWorld().getEntityById(id);
            if (e instanceof LivingEntity living) return living;
        }
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
