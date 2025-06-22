package com.collective.projectcore.util;

import com.collective.projectcore.blocks.enrichment.ScratchingPostEnrichmentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class UtilMethods {

    public static String sortStringUppercase(String string) {
        StringBuilder newstr = new StringBuilder();
        StringBuilder upper = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isUpperCase(string.charAt(i))) {
                upper.append(string.charAt(i));
            }
            else {
                newstr.append(string.charAt(i));
            }
        }
        return upper+ newstr.toString();
    }

    public static int getBlockColour(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex) {
        if (tintIndex == 0 && state.contains(ScratchingPostEnrichmentBlock.COLOUR)) {
            return state.get(ScratchingPostEnrichmentBlock.COLOUR).getFireworkColor();
        }
        return -1;
    }
}
