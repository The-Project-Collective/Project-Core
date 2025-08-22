package com.collective.projectcore.util.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class GuiRenderHelper {

    /** Helper for rendering a single scaled item. */
    public static void drawItem(DrawContext context, ItemStack stack, int x, int y, float scale) {
        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 100.0F);
        matrices.scale(scale, scale, scale);
        context.drawItem(stack, 0, 0);
        matrices.pop();
    }

    /**
     * Renders a scrollable item grid with support for mouse wheel scrolling only.
     *
     * @param context        DrawContext for rendering.
     * @param items          List of items to render.
     * @param startX         Grid start X coordinate.
     * @param startY         Grid start Y coordinate.
     * @param cols           Number of columns in the grid.
     * @param rowsVisible    Number of visible rows.
     * @param spacing        Pixel spacing between items.
     * @param scale          Item render scale.
     * @param scrollOffset   Current scroll offset (in pixels).
     * @param mouseWheelDelta Mouse wheel delta (positive/negative for scroll).
     * @return Updated scrollOffset
     */
    public static int drawScrollableItemGrid(
            DrawContext context,
            List<Item> items,
            int startX, int startY,
            int cols, int rowsVisible,
            int spacing,
            float scale,
            int scrollOffset,
            float mouseWheelDelta
    ) {
        int totalRows = (int) Math.ceil(items.size() / (double) cols);
        int gridHeight = rowsVisible * spacing;
        int maxScroll = Math.max(0, (totalRows - rowsVisible) * spacing);
        int gridWidth = cols * spacing;
        scrollOffset = (int) (scrollOffset - (mouseWheelDelta * 10));
        scrollOffset = clamp(scrollOffset, 0, maxScroll);
        context.enableScissor(startX, startY, startX + gridWidth, startY + gridHeight);
        for (int i = 0; i < items.size(); i++) {
            int col = i % cols;
            int row = i / cols;

            int x = startX + col * spacing;
            int y = startY + row * spacing - scrollOffset;

            if (y + spacing < startY || y > startY + gridHeight) continue;

            drawItem(context, new ItemStack(items.get(i)), x, y, scale);
        }
        context.disableScissor();
        int scrollbarX = startX + gridWidth + 2;
        int scrollbarWidth = 2;
        if (items.size() > cols * rowsVisible) {
            fillRect(context, scrollbarX, startY, scrollbarWidth, gridHeight, 0xFFF5D69F);
            if (maxScroll > 0) {
                int thumbHeight = Math.max(10, gridHeight * rowsVisible / totalRows);
                int thumbY = startY + (int) ((gridHeight - thumbHeight) * (scrollOffset / (float) maxScroll));
                fillRect(context, scrollbarX, thumbY, scrollbarWidth, thumbHeight, 0xFF3A7F6E);
            }
        }
        return scrollOffset;
    }

    /**
     * Renders a scrollable text grid with support for mouse wheel scrolling only.
     *
     * @param context        DrawContext for rendering.
     * @param text          List of text to render.
     * @param startX         Grid start X coordinate.
     * @param startY         Grid start Y coordinate.
     * @param cols           Number of columns in the grid.
     * @param rowsVisible    Number of visible rows.
     * @param spacing        Pixel spacing between text.
     * @param scale          Text render scale.
     * @param scrollOffset   Current scroll offset (in pixels).
     * @param mouseWheelDelta Mouse wheel delta (positive/negative for scroll).
     * @return Updated scrollOffset
     */
    public static int drawScrollableTextGrid(
            DrawContext context,
            List<Text> text,
            int startX, int startY,
            int cols, int rowsVisible,
            int spacing,
            float scale,
            int scrollOffset,
            float mouseWheelDelta
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        int ySpacing = 8;
        int totalRows = (int) Math.ceil(text.size() / (double) cols);
        int gridHeight = rowsVisible * ySpacing;
        int maxScroll = Math.max(0, (totalRows - rowsVisible) * ySpacing);
        int gridWidth = cols * spacing;
        scrollOffset = (int) (scrollOffset - (mouseWheelDelta * 10));
        scrollOffset = clamp(scrollOffset, 0, maxScroll);
        context.enableScissor(startX, startY, startX + gridWidth, startY + gridHeight);
        for (int i = 0; i < text.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * spacing;
            int y = startY + row * ySpacing - scrollOffset;
            if (y + spacing < startY || y > startY + gridHeight) continue;
            drawScaledText(context, textRenderer, text.get(i), x, y, scale, 0xFF397064);
        }
        context.disableScissor();
        int scrollbarX = startX + gridWidth + 3;
        int scrollbarWidth = 2;
        if (text.size() > cols * rowsVisible) {
            fillRect(context, scrollbarX, startY, scrollbarWidth, gridHeight, 0xFFF5D69F);
            if (maxScroll > 0) {
                int thumbHeight = Math.max(10, gridHeight * rowsVisible / totalRows);
                int thumbY = startY + (int) ((gridHeight - thumbHeight) * (scrollOffset / (float) maxScroll));
                fillRect(context, scrollbarX, thumbY, scrollbarWidth, thumbHeight, 0xFF3A7F6E);
            }
        }
        return scrollOffset;
    }

    private static void fillRect(DrawContext context, int x, int y, int w, int h, int color) {
        context.fill(x, y, x + w, y + h, color);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void drawScaledText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, float scale, int color) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(textRenderer, text, 0, 0, color, false);
        context.getMatrices().pop();
    }
}

