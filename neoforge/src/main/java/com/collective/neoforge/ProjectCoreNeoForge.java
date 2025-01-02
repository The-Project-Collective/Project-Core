package com.collective.neoforge;

import net.neoforged.fml.common.Mod;

import com.collective.ProjectCore;

@Mod(ProjectCore.MOD_ID)
public final class ProjectCoreNeoForge {
    public ProjectCoreNeoForge() {
        // Run our common setup.
        ProjectCore.init();
    }
}
