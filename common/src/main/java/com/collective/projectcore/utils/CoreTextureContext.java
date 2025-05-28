package com.collective.projectcore.utils;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.entities.base.CoreAnimalEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * An interface for the calculation of custom entity textures.
 * First, instantiate this class in each creature class that requires it.
 * Then, have each of your gene records implement this class.
 * @see BaseGeneTexture
 * Then fill in the details for the record by implementing the required methods.
 * Use AmericanRedFoxEntity in Project Wildlife as an example.
 */
public interface CoreTextureContext {

    Logger LOGGER = LogManager.getLogManager().getLogger(ProjectCore.MOD_ID);

    List<BaseGeneTexture> geneTextures();
    String animalName();

    /**
     * Default method to colour entity textures that can be used by everything that extends CoreAnimalEntity.
     * This is the method that should be used!!! Don't directly use the colourSpecificEntities method!
     *
     * @param entity the entity being textured.
     * @return the final NativeImage for the entity texture.
     * @throws IOException if the result is null.
     */
    default NativeImage colourEntity(CoreAnimalEntity entity) throws IOException {
        return colourSpecificEntities(entity, geneTextures(), animalName());
    }

    /**
     * The method to be overrided when CoreTextureContext is instantiated in order to account for different entities having different base textures.
     *
     * @param entity the entity being textured.
     * @param textures the base gene texture locations.
     * @param name the name of the entity used to sort out how to texture different entities.
     * @return the final Native Image for the entity texture.
     * @throws IOException if the result is null.
     */
    NativeImage colourSpecificEntities(CoreAnimalEntity entity, List<BaseGeneTexture> textures, String name) throws IOException;

    /**
     * Used to get the base textures for further processing.
     *
     * @param location the location in the textures/ folder of the base textures.
     * @return the image from the base location.
     */
    default NativeImage getNativeImageFromResourceLocation(Identifier location) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(location);
            return NativeImage.read(resource.getInputStream());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Couldn't load image", e);
        }
        LOGGER.log(Level.WARNING, "Skipping this image due to error: "+this);
        return null;
    }

    /**
     * Combines images in a 1:1 ratio.
     * The overlay image will always be drawn on top of the base image.
     *
     * @param base image.
     * @param overlay image.
     */
    default void combineImages(NativeImage base, NativeImage overlay) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for(int x = 0; x < base.getWidth(); ++x) {
                int image_colour = overlay.getColorArgb(x, y);
                if (ColorHelper.getAlpha(image_colour) > 0) {
                    base.setColorArgb(x, y, image_colour);
                }
            }
        }
    }

    default void stainLayer(NativeImage base, Color colour) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for(int x = 0; x < base.getWidth(); ++x) {
                int colourRGB = colour.getRGB();
                int alpha = ColorHelper.getAlpha(base.getColorArgb(x, y));
                Color base_colour = new Color(base.getColorArgb(x, y));
                Color new_colour = new Color(this.multiply(base_colour.getRGB(), colourRGB, 1, 1));

                base.setColorArgb(x, y, this.stainViaLuminance(alpha, base_colour, new_colour));
            }
        }
    }

    default int stainViaLuminance(int alpha, Color colour1, Color colour2) {
        int R = colour1.getRed();
        int G = colour1.getGreen();
        int B = colour1.getBlue();

        // Standard Luminance Calculation:
        float L = (float) ((0.2126*R) + (0.7152*G) + (0.0722*B));

        int finalRed = (int) ((colour2.getRed() * L) / 255);
        int finalGreen = (int) ((colour2.getGreen() * L) / 255);
        int finalBlue = (int) ((colour2.getBlue() * L) / 255);

        return ColorHelper.getArgb(alpha, finalRed, finalGreen, finalBlue);
    }

    /**
     * Multiply the base and overlay images together.
     * This combines colours and usually results in a darker image.
     *
     * @param base image
     * @param overlay image.
     * @param brightness value to offset normal multiplying darkening.
     */
    default void multiplyImages(NativeImage base, NativeImage overlay, float brightness) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for (int x = 0; x < base.getWidth(); ++x) {
                int base_colour = base.getColorArgb(x, y);
                int overlay_colour = overlay.getColorArgb(x, y);
                if (ColorHelper.getAlpha(overlay_colour) > 0) {
                    int overlay_alpha = ColorHelper.getAlpha(overlay_colour);
                    base.setColorArgb(x, y, multiply(base_colour, overlay_colour, overlay_alpha/255f, brightness));
                }
            }
        }
    }

    /**
     * Combines the base and overlay images together using the soft luminance of the overlay.
     * This usually results in a lighter image.
     *
     * @param base image.
     * @param overlay image.
     */
    default void softLightImages(NativeImage base, NativeImage overlay) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for (int x = 0; x < base.getWidth(); ++x) {
                int base_colour = base.getColorArgb(x, y);
                int overlay_colour = overlay.getColorArgb(x, y);
                if (ColorHelper.getAlpha(overlay_colour) > 0) {
                    int overlay_alpha = ColorHelper.getAlpha(overlay_colour);
                    base.setColorArgb(x, y, softLight(base_colour, overlay_colour, overlay_alpha/255f));
                }
            }
        }
    }

    /**
     * Multiply two colours together. Usually results in a darker final colour.
     *
     * @param baseColour initial colour.
     * @param overlayColour overlay colour.
     * @param opacity opacity / strength of the overlay colour.
     * @return the multiplied colour.
     */
    default int multiply(int baseColour, int overlayColour, float opacity, float brightness) {
        int a = ColorHelper.getAlpha(baseColour);
        int r = ColorHelper.getRed(baseColour);
        int g = ColorHelper.getGreen(baseColour);
        int b = ColorHelper.getBlue(baseColour);
        int ro = ColorHelper.getRed(overlayColour);
        int go = ColorHelper.getGreen(overlayColour);
        int bo = ColorHelper.getBlue(overlayColour);
        int rf = (int) Math.min(255, ((float) (r * ro) / 255)  * brightness);
        int gf = (int) Math.min(255, ((float) (g * go) / 255)  * brightness);
        int bf = (int) Math.min(255, ((float) (b * bo) / 255)  * brightness);
        rf = Math.round((1 - opacity) * r + opacity * rf);
        gf = Math.round((1 - opacity) * g + opacity * gf);
        bf = Math.round((1 - opacity) * b + opacity * bf);
        return ColorHelper.getArgb(a, rf, gf, bf);
    }

    /**
     * Combine two colours together using the soft luminance of the overlay. Usually results in a lighter final colour.
     *
     * @param baseColour initial colour.
     * @param overlayColour overlay colour.
     * @param opacity opacity / strength of the overlay colour.
     * @return the soft lighted colour.
     */
    default int softLight(int baseColour, int overlayColour, float opacity) {
        int a = ColorHelper.getAlpha(baseColour);
        int r = ColorHelper.getRed(baseColour);
        int g = ColorHelper.getGreen(baseColour);
        int b = ColorHelper.getBlue(baseColour);
        int ro = ColorHelper.getRed(overlayColour);
        int go = ColorHelper.getGreen(overlayColour);
        int bo = ColorHelper.getBlue(overlayColour);

        int rf = blendSoftLightChannel(r, ro, opacity);
        int gf = blendSoftLightChannel(g, go, opacity);
        int bf = blendSoftLightChannel(b, bo, opacity);

        return ColorHelper.getArgb(a, rf, gf, bf);
    }

    /**
     * A helper method for calculating soft light blending an individual RGB channel.
     * This method is only used in the softLight method.
     *
     * @param base initial colour.
     * @param overlay overlay colour.
     * @param opacity opacity / strength of the overlay colour.
     * @return the soft lighted channel colour.
     */
    private int blendSoftLightChannel(int base, int overlay, float opacity) {
        int blended;
        if (overlay < 128) {
            blended = (2 * base * overlay) / 255 + (base * base * (255 - 2 * overlay)) / (255 * 255);
        } else {
            blended = (int) (Math.sqrt(base / 255.0) * (2 * overlay - 255) + 2 * base * (255 - overlay) / 255.0);
        }
        blended = Math.min(255, Math.max(0, blended));
        return Math.round((1 - opacity) * base + opacity * blended);
    }

    /**
     * Combine two colours together using the hard luminance of the overlay. Usually results in a lighter or "washed out" final colour.
     *
     * @param baseColour initial colour.
     * @param overlayColour overlay colour.
     * @param opacity opacity / strength of the overlay colour.
     * @return the screened colour.
     */
    default int[] screen(int baseColour, int overlayColour, float opacity) {
        int r = ColorHelper.getRed(baseColour);
        int g = ColorHelper.getGreen(baseColour);
        int b = ColorHelper.getBlue(baseColour);
        int ro = ColorHelper.getRed(overlayColour);
        int go = ColorHelper.getGreen(overlayColour);
        int bo = ColorHelper.getBlue(overlayColour);
        int rf = 255 - (((255 - ro) * (255 - r)) / 255);
        int gf = 255 - (((255 - go) * (255 - g)) / 255);
        int bf = 255 - (((255 - bo) * (255 - b)) / 255);
        //rf = Math.round((1 - opacity) * r + opacity * rf);
        //gf = Math.round((1 - opacity) * g + opacity * gf);
        //bf = Math.round((1 - opacity) * b + opacity * bf);
        return new int[]{rf, gf, bf};
    }

    /**
     * Implemented by gene records created in specific entity classes to be used by <code>CoreTextureContext</code>.
     */
    interface BaseGeneTexture {
        String name();
        String relativeTexturePath();
        String modID();
        HashMap<String, String> textures();
        HashMap<String, Color> colours();
        default Identifier identifier() {
            return Identifier.of(modID(), "textures/entity/"+relativeTexturePath());
        }
    }
}
