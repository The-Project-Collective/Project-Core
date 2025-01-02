package com.collective.neoforge.client;

import net.neoforged.bus.api.IEventBus;

//@Mod(value = ProjectCore.MOD_ID, dist = Dist.CLIENT)
public class ProjectCoreNeoForgeClient {

    public ProjectCoreNeoForgeClient(IEventBus modBus) {
        modBus.register(this);
    }

}
