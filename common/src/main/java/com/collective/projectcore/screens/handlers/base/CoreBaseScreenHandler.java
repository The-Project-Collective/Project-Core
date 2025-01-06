package com.collective.projectcore.screens.handlers.base;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public abstract class CoreBaseScreenHandler extends ScreenHandler {

    protected CoreBaseScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
}
