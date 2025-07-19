package com.collective.projectcore;

import com.collective.projectcore.util.networking.CoreClientPacketHandler;

public class ProjectCoreClientCommon {

    public static void registerCommonClient() {
        CoreClientPacketHandler.registerPackets();
    }
}
