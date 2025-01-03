package com.collective.projectcore.entities.variant;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface VariantContext {

    List<VariantMorph> morphs();
    String calculateWildFunc();
    default String calculateVariant(String parent1, String parent2) {
        Random random = new Random();
        int parent1Light = 1;
        int parent2Light = 1;
        for (VariantMorph a : morphs()) {
            if (a.name().equals(parent1)) {
                parent1Light = a.lightValue();
            }
            if (a.name().equals(parent2)) {
                parent2Light = a.lightValue();
            }
        }
        int totalLightValue = parent1Light + parent2Light;
        List<VariantMorph> possibleMorphs = new ArrayList<>();
        for (VariantMorph c : morphs()) {
            if (c.possibleLightLevels().contains(totalLightValue)) {
                // Picks a morph based on...
                // ...the rarity (defined below) +1 (to account for 0 rarities)...
                // ...and then that times rarity + itself to create an exponential curve.
                // Wanted to do rarity+1 * rarity*rarity, but I think the high numbers would be too resource intensive...
                for (int i = 0; i <= (c.rarity()+1)*(c.rarity()+c.rarity()); i++) {
                    possibleMorphs.add(c);
                }
            }
        }
        return possibleMorphs.get(random.nextInt(possibleMorphs.size())).name();
    }

    interface VariantMorph {
        String name();
        int lightValue();
        List<Integer> possibleLightLevels();
        int rarity();
        boolean isLight();
        boolean isDark();
        String relativeTexturePath();
        String modID();
        default Identifier identifier() {
            return Identifier.of(modID(), "textures/entity/"+relativeTexturePath());
        }
    }
}
