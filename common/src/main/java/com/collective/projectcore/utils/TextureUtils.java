package com.collective.projectcore.utils;

import com.collective.projectcore.ProjectCore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TextureUtils {

    Logger LOGGER = LogManager.getLogManager().getLogger(ProjectCore.MOD_ID);

    public NativeImage getNativeImageFromResourceLocation(Identifier location) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(location);
            return NativeImage.read(resource.getInputStream());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Couldn't load image", e);
        }
        LOGGER.log(Level.WARNING, "Skipping this image due to error: "+this);
        return null;
    }

    public void combineLayers(NativeImage base, NativeImage image) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for(int x = 0; x < base.getWidth(); ++x) {
                int image_colour = image.getColorArgb(x, y);
                if (ColorHelper.getAlpha(image_colour) > 0) {
                    base.setColorArgb(x, y, image_colour);
                }
            }
        }
    }

    public void combineLayersBlend(NativeImage base, NativeImage image, double weight) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for(int x = 0; x < base.getWidth(); ++x) {
                int base_colour = base.getColorArgb(x, y);
                int image_colour = image.getColorArgb(x, y);
                if (ColorHelper.getAlpha(image_colour) > 0) {
                    int final_colour = blendColour(new Color(base_colour), new Color(image_colour), weight).getRGB();
                    base.setColorArgb(x, y, final_colour);
                }
            }
        }
    }

    public Color blendColour(Color colour1, Color colour2, double weight) {
        float r = (float) weight;
        float ir = (float) 1.0 - r;

        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];

        colour1.getColorComponents(rgb1);
        colour2.getColorComponents(rgb2);

        return new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);

    }

    public void stainLayerDiffBase(NativeImage base, NativeImage luminanceLayer, Color colour) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for(int x = 0; x < base.getWidth(); ++x) {
                int colourRGB = colour.getRGB();
                int alpha = ColorHelper.getAlpha(base.getColorArgb(x, y));
                Color base_colour = new Color(base.getColorArgb(x, y));
                Color new_colour = new Color(this.multiply(base_colour.getRGB(), colourRGB));
                Color luminance_colour = new Color(luminanceLayer.getColorArgb(x, y));

                base.setColorArgb(x, y, this.stainViaLuminance(alpha, luminance_colour, new_colour));
            }
        }
    }

    public void stainLayer(NativeImage base, Color colour) {
        for(int y = 0; y < base.getHeight(); ++y) {
            for(int x = 0; x < base.getWidth(); ++x) {
                int colourRGB = colour.getRGB();
                int alpha = ColorHelper.getAlpha(base.getColorArgb(x, y));
                Color base_colour = new Color(base.getColorArgb(x, y));
                Color new_colour = new Color(this.multiply(base_colour.getRGB(), colourRGB));

                base.setColorArgb(x, y, this.stainViaLuminance(alpha, base_colour, new_colour));
            }
        }
    }

    public int stainViaLuminance(int alpha, Color colour1, Color colour2) {
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

    public int multiply(int color, int baseColour) {
        int a = ColorHelper.getAlpha(color);
        int r = ColorHelper.getRed(color);
        r = (int)((float)r * ColorHelper.getRed(baseColour)) / 255;
        int g = ColorHelper.getGreen(color);
        g = (int)((float)g * ColorHelper.getGreen(baseColour)) / 255;
        int b = ColorHelper.getBlue(color);
        b = (int)((float)b * ColorHelper.getBlue(baseColour)) / 255;
        return ColorHelper.getArgb(a, r, g, b);
    }
}
