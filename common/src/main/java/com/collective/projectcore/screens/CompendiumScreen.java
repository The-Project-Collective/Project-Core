package com.collective.projectcore.screens;

import com.collective.projectcore.ProjectCore;
import com.collective.projectcore.entities.CoreAnimalEntity;
import com.collective.projectcore.screens.handlers.CompendiumScreenHandler;
import com.collective.projectcore.util.UtilMethods;
import com.collective.projectcore.util.rendering.GuiRenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public class CompendiumScreen extends HandledScreen<CompendiumScreenHandler> {

    private static final Identifier BASE_TEXTURE = Identifier.of(ProjectCore.MOD_ID, "textures/gui/compendium.png");

    private int currentPage = 0;
    private TexturedButtonWidget page1;
    private TexturedButtonWidget page2;
    private TexturedButtonWidget page3;
    private TexturedButtonWidget page4;

    private int scrollOffset = 0;
    private float mouseWheelDelta = 0;

    public CompendiumScreen(CompendiumScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 278;
        backgroundHeight = 179;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int x = this.x + 162;
        int y = this.y + 14;
        page1 = this.addDrawableChild(new TexturedButtonWidget(
                x, y, 19, 14,
                new ButtonTextures(
                        Identifier.of(ProjectCore.MOD_ID, "compendium/stats_off"),
                        Identifier.of(ProjectCore.MOD_ID, "compendium/stats_on")),
                b -> {
                    currentPage = 0;
                },
                Text.empty()
        ));
        page2 = this.addDrawableChild(new TexturedButtonWidget(
                x + 22, y, 19, 14,
                new ButtonTextures(
                        Identifier.of(ProjectCore.MOD_ID, "compendium/genetics_off"),
                        Identifier.of(ProjectCore.MOD_ID, "compendium/genetics_on")),
                b -> {
                    currentPage = 1;
                },
                Text.empty()
        ));
        page3 = this.addDrawableChild(new TexturedButtonWidget(
                x + 45, y, 19, 14,
                new ButtonTextures(
                        Identifier.of(ProjectCore.MOD_ID, "compendium/info_off"),
                        Identifier.of(ProjectCore.MOD_ID, "compendium/info_on")),
                b -> {
                    currentPage = 2;
                },
                Text.empty()
        ));
        page4 = this.addDrawableChild(new TexturedButtonWidget(
                x + 67, y, 19, 14,
                new ButtonTextures(
                        Identifier.of(ProjectCore.MOD_ID, "compendium/irl_off"),
                        Identifier.of(ProjectCore.MOD_ID, "compendium/irl_on")),
                b -> {
                    currentPage = 3;
                },
                Text.empty()
        ));
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        LivingEntity entity = handler.getEntity();
        if (entity == null || !entity.isAlive() || entity.getWorld() != MinecraftClient.getInstance().world) {
            MinecraftClient.getInstance().setScreen(null);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        LivingEntity entity = handler.getEntity();
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        if (entity != null && entity.isAlive()) {
            renderEntity(context, this.x + 18 , this.y + 30, this.x + 130, this.y + 118, entity instanceof CoreAnimalEntity animal ? animal.getCompendiumDisplaySize() : 30, 0.0625F, mouseX, mouseY, entity);
        }
        int selectionBoxX = this.x + 159;
        int selectionBoxY = this.y + 27;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        switch (currentPage) {
            case 1 -> context.drawTexture(RenderLayer::getGuiTextured, BASE_TEXTURE, selectionBoxX, selectionBoxY, 373, 0, 92, 126, 512, 256);
            case 2 -> context.drawTexture(RenderLayer::getGuiTextured, BASE_TEXTURE, selectionBoxX, selectionBoxY, 280, 127, 92, 126, 512, 256);
            case 3 -> context.drawTexture(RenderLayer::getGuiTextured, BASE_TEXTURE, selectionBoxX, selectionBoxY, 373, 127, 92, 126, 512, 256);
            default -> context.drawTexture(RenderLayer::getGuiTextured, BASE_TEXTURE, selectionBoxX, selectionBoxY, 280, 0, 92, 126, 512, 256);
        }
        page1.active = currentPage != 0;
        page2.active = currentPage != 1;
        page3.active = currentPage != 2;
        page4.active = currentPage != 3;
        page1.setFocused(!page1.active);
        page2.setFocused(!page2.active);
        page3.setFocused(!page3.active);
        page4.setFocused(!page4.active);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        LivingEntity entity = handler.getEntity();
        int page1XCentre = 75;
        int page1YSection = 132;
        int page2XCentre = 205;
        int page2TextX = 165;
        int page2TextY = 55;
        int page2TextYBottom = 142;
        int textColour = 0x397064;
        if (entity != null && entity.isAlive()) {
            this.drawScaledCentredText(context, this.textRenderer, Text.translatable(entity.getType().getTranslationKey()), page1XCentre, 21, 0.8f, textColour);
            if (entity instanceof CoreAnimalEntity animal) {
                this.drawScaledCentredText(context, this.textRenderer, Text.translatable(animal.getScientificName()).formatted(Formatting.ITALIC), page1XCentre, page1YSection, 0.65f, textColour);
            }
            if (entity.hasCustomName()) {
                this.drawScaledCentredText(context, this.textRenderer, Text.translatable("screen.project_core.creature_compendium.custom_name", Objects.requireNonNull(entity.getCustomName()).getString()), page1XCentre, page1YSection + 10, 0.65f, textColour);
            }
            if (entity instanceof CoreAnimalEntity animal) {
                this.drawScaledCentredText(context, this.textRenderer, (!animal.getOwnerDisplayName().isEmpty() ? Text.translatable("screen.project_core.creature_compendium.owner", animal.getOwnerDisplayName()) : Text.translatable("screen.project_core.creature_compendium.owner.untamed")), page1XCentre, page1YSection + 20, 0.65f, textColour);
            }
            switch (currentPage) {
                case 0 -> {
                    this.drawScaledCentredText(context, this.textRenderer, Text.translatable("screen.project_core.creature_compendium.stats"), page2XCentre, 42, 0.65f, textColour);
                    this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.health", entity.getHealth(), entity.getMaxHealth()), page2TextX, page2TextY, 0.65f, textColour);
                    if (entity instanceof CoreAnimalEntity animal) {
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.age", animal.getAgeDays()), page2TextX, page2TextY + 8, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.gender", animal.getGender() == 0 ? Text.translatable("screen.project_core.creature_compendium.gender.male") : Text.translatable("screen.project_core.creature_compendium.gender.female")), page2TextX, page2TextY + 16, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.hunger", animal.getHunger(), animal.getMaxFood()), page2TextX, page2TextY + 24, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.enrichment", animal.getEnrichment(), animal.getMaxEnrichment()), page2TextX, page2TextY + 32, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.pregnant", animal.isPregnant() ? Text.translatable("screen.project_core.creature_compendium.true") : Text.translatable("screen.project_core.creature_compendium.false")), page2TextX, page2TextY + 48, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.stunted", animal.isStunted() ? Text.translatable("screen.project_core.creature_compendium.true") : Text.translatable("screen.project_core.creature_compendium.false")), page2TextX, page2TextY + 56, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.contraceptive", animal.hasContraceptives() ? Text.translatable("screen.project_core.creature_compendium.true") : Text.translatable("screen.project_core.creature_compendium.false")), page2TextX, page2TextY + 64, 0.65f, textColour);
                    }
                }
                case 1 -> {
                    this.drawScaledCentredText(context, this.textRenderer, Text.translatable("screen.project_core.creature_compendium.genetics"), page2XCentre, 42, 0.65f, textColour);
                    if (entity instanceof CoreAnimalEntity animal) {
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.genes"), page2TextX, page2TextY, 0.65f, textColour);
                        if (animal.hasGenetics()) {
                            scrollOffset = GuiRenderHelper.drawScrollableTextGrid(
                                    context,
                                    animal.getCompendiumGenes(),
                                    page2TextX, page2TextY + 12,
                                    1, 5,
                                    76,
                                    0.65f,
                                    scrollOffset,
                                    mouseWheelDelta
                            );
                            mouseWheelDelta = 0;
                            this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.genome.size", animal.evaluateStatGenetics(animal.getSizeGeneIndex())), page2TextX, page2TextYBottom - 24, 0.65f, textColour);
                            this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.genome.health", animal.evaluateStatGenetics(animal.getAttributeGeneIndex(EntityAttributes.MAX_HEALTH))), page2TextX, page2TextYBottom - 16, 0.65f, textColour);
                            this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.genome.speed", animal.evaluateStatGenetics(animal.getAttributeGeneIndex(EntityAttributes.MOVEMENT_SPEED))), page2TextX, page2TextYBottom - 8, 0.65f, textColour);
                            this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.genome.attack_damage", animal.evaluateStatGenetics(animal.getAttributeGeneIndex(EntityAttributes.ATTACK_DAMAGE))), page2TextX, page2TextYBottom, 0.65f, textColour);
                        } else {
                            this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.na", entity.getHealth(), entity.getMaxHealth()), page2TextX, page2TextY, 0.65f, textColour);
                        }
                    }
                }
                case 2 -> {
                    this.drawScaledCentredText(context, this.textRenderer, Text.translatable("screen.project_core.creature_compendium.info"), page2XCentre, 42, 0.65f, textColour);
                    if (entity instanceof CoreAnimalEntity animal) {
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.adult_age", animal.getAdultDays()), page2TextX, page2TextY, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.monogamy", animal.isMonogamous() ? Text.translatable("screen.project_core.creature_compendium.true") : Text.translatable("screen.project_core.creature_compendium.false")), page2TextX, page2TextY + 8, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.group_size", animal.getMaxGroupSize()), page2TextX, page2TextY + 16, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.offspring", animal.getMinOffspring(), animal.getMaxOffspring(), animal.rareOffspring() ? Text.translatable("screen.project_core.creature_compendium.offspring.rare") : ""), page2TextX, page2TextY + 24, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.schedule"), page2TextX, page2TextY + 32, 0.65f, textColour);
                        this.drawScaledText(context, animal.getScheduleName(), page2TextX, page2TextY + 40, 0.65f, textColour);
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.diet.favourite"), page2TextX, page2TextY + 48, 0.65f, textColour);
                        int startXFav = page2TextX + 56;
                        int startYFav = page2TextY + 47;
                        int spacing = 9;
                        List<Item> fav_food_items = UtilMethods.getItemsFromTag(animal.getSpecificDiet());
                        for (int i = 0; i < fav_food_items.size(); i++) {
                            ItemStack stack = new ItemStack(fav_food_items.get(i));
                            int x = startXFav + (i % 3) * spacing;
                            int y = startYFav + (i / 3) * spacing;
                            GuiRenderHelper.drawItem(context, stack, x, y, 0.5f);
                        }
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.diet"), page2TextX, page2TextY + 56, 0.65f, textColour);
                        int startX = page2TextX - 1;
                        int startY = page2TextY + 64;
                        scrollOffset = GuiRenderHelper.drawScrollableItemGrid(
                                context,
                                UtilMethods.getItemsFromTag(animal.getGeneralDiet()),
                                startX, startY,
                                9, 3,
                                spacing,
                                0.5f,
                                scrollOffset,
                                mouseWheelDelta
                        );
                        mouseWheelDelta = 0;
                    }
                }
                case 3 -> {
                    this.drawScaledCentredText(context, this.textRenderer, Text.translatable("screen.project_core.creature_compendium.irl"), page2XCentre, 42, 0.65f, textColour);
                    if (entity instanceof CoreAnimalEntity animal) {
                        this.drawScaledText(context, Text.translatable("screen.project_core.creature_compendium.conservation_status", animal.getConservationStatus()), page2TextX, page2TextY, 0.6f, textColour);
                        scrollOffset = GuiRenderHelper.drawScrollableTextGrid(
                                context,
                                animal.getIRLInfo(),
                                page2TextX, page2TextY + 12,
                                1, 10,
                                76,
                                0.5f,
                                scrollOffset,
                                mouseWheelDelta
                        );
                        mouseWheelDelta = 0;
                    }
                }
            }
        }
    }

    public void drawScaledText(DrawContext context, Text text, int x, int y, float scale, int color) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(this.textRenderer, text, 0, 0, color, false);
        context.getMatrices().pop();
    }

    public void drawScaledCentredText(DrawContext context, TextRenderer textRenderer, Text text, int centerX, int centerY, float scale, int color) {
        context.getMatrices().push();
        context.getMatrices().translate(centerX, centerY, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        int textWidth = textRenderer.getWidth(text);
        int textHeight = textRenderer.fontHeight;
        float x = -(textWidth / 2.0f);
        float y = -(textHeight / 2.0f);
        context.drawText(textRenderer, text, (int)x, (int)y, color, false);
        context.getMatrices().pop();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.3F);
        RenderSystem.setShaderTexture(0, BASE_TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(RenderLayer::getGuiTextured, BASE_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 512, 256);
    }

    public static void renderEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float f, float mouseX, float mouseY, LivingEntity entity) {
        float g = (float)(x1 + x2) / 2.0F;
        float h = (float)(y1 + y2) / 2.0F;
        context.enableScissor(x1, y1, x2, y2);
        float i = (float)Math.atan((g - mouseX) / 40.0F);
        float j = (float)Math.atan((h - mouseY) / 40.0F);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(j * 20.0F * 0.017453292F);
        quaternionf.mul(quaternionf2);
        float k = entity.bodyYaw;
        float l = entity.getYaw();
        float m = entity.getPitch();
        float n = entity.prevHeadYaw;
        float o = entity.headYaw;
        entity.bodyYaw = 180.0F + i * 20.0F;
        entity.setYaw(180.0F + i * 40.0F);
        entity.setPitch(-j * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        float p = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + f * p, 0.0F);
        float q = (float)size / p;
        drawEntity(context, g, h, q, vector3f, quaternionf, quaternionf2, entity);
        entity.bodyYaw = k;
        entity.setYaw(l);
        entity.setPitch(m);
        entity.prevHeadYaw = n;
        entity.headYaw = o;
        context.disableScissor();
    }

    public static void drawEntity(DrawContext context, float x, float y, float size, Vector3f vector3f, Quaternionf quaternionf, @Nullable Quaternionf quaternionf2, LivingEntity entity) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 50.0);
        context.getMatrices().scale(size, size, -size);
        context.getMatrices().translate(vector3f.x, vector3f.y, vector3f.z);
        context.getMatrices().multiply(quaternionf);
        context.draw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        if (quaternionf2 != null) {
            entityRenderDispatcher.setRotation(quaternionf2.conjugate(new Quaternionf()).rotateY(3.1415927F));
        }
        entityRenderDispatcher.setRenderShadows(false);
        context.draw((vertexConsumers) -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 1.0F, context.getMatrices(), vertexConsumers, 15728880));
        context.draw();
        entityRenderDispatcher.setRenderShadows(true);
        context.getMatrices().pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        mouseWheelDelta = (float) verticalAmount;
        return true;
    }

}
