package com.collective.projectcore.entities.variant;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An interface for the calculation and application of variants for entities that require them.
 * First, instantiate this class in each creature class that requires it.
 * Then, for each type of colour morph, create a record in your entity class which implements...
 * @see VariantMorph
 * Then fill in the details for the record by implementing the required methods.
 * Use AmericanRedFoxEntity in Project Wildlife as an example.
 */
public interface VariantContext {

    /**
     * Contains all entity-specific morph records.
     */
    List<VariantMorph> morphs();

    /**
     * Determines the rarity of wild-spawning variants.
     * Will need to be defined when VariantContext is created.
     */
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
                for (int i = 0; i <= (c.rarity()+1)*(c.rarity()+c.rarity()); i++) {
                    possibleMorphs.add(c);
                }
            }
        }
        return possibleMorphs.get(random.nextInt(possibleMorphs.size())).name();
    }

    /**
     * Implemented by morph records created in specific entity classes to be used by <code>VariantContext</code>.
     */
    interface VariantMorph {
        String name();
        int lightValue();
        List<Integer> possibleLightLevels();
        int rarity();
        String relativeTexturePath();
        String modID();
        boolean isLight();
        boolean isDark();
        default Identifier identifier() {
            return Identifier.of(modID(), "textures/entity/"+relativeTexturePath());
        }
    }
}
