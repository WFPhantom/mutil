package se.mickelus.mutil.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class GuiItem extends GuiElement {
    private final Minecraft mc;

    private ItemStack itemStack;

    private boolean showTooltip = true;
    private CountMode countMode = CountMode.normal;

    private float opacityThreshold = 1;

    private boolean renderDecoration = true;

    public GuiItem(int x, int y) {
        super(x, y, 16, 16);

        mc = Minecraft.getInstance();

        setVisible(false);
    }

    /**
     * Sets the opacity threshold for this element, the item will only render when the combined opacity of this element and it's parent is above the
     * threshold.
     *
     * @param opacityThreshold
     * @return
     */
    public GuiItem setOpacityThreshold(float opacityThreshold) {
        this.opacityThreshold = opacityThreshold;
        return this;
    }

    public GuiItem setTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        return this;
    }

    public GuiItem setCountVisibility(CountMode mode) {
        this.countMode = mode;
        return this;
    }

    public GuiItem setItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        setVisible(itemStack != null);
        return this;
    }

    public GuiItem setRenderDecoration(boolean shouldRender) {
        this.renderDecoration = shouldRender;
        return this;
    }

    // todo 1.20: blitOffset gone, still works?
    // todo 1.20: how to render decorations?
    @Override
    public void draw(final GuiGraphicsExtractor graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        super.draw(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
        if (itemStack != null && opacity * getOpacity() >= opacityThreshold) {
            graphics.item(itemStack, refX + x, refY + y);

            if (renderDecoration) {
                graphics.itemDecorations(mc.font, itemStack, refX + x, refY + y, getCountString());
            }
        }
    }

    protected String getCountString() {
        return switch (countMode) {
            case normal -> null;
            case always -> String.valueOf(itemStack.getCount());
            case never -> "";
        };
    }

    @Override
    public List<Component> getTooltipLines() {
        if (showTooltip && itemStack != null && hasFocus()) {
            return new ArrayList<>(itemStack.getTooltipLines(Item.TooltipContext.of(mc.level), Minecraft.getInstance().player,
                    mc.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL));
        }

        return null;
    }

    public enum CountMode {
        normal, // shows if count is > 1

        always,
        never
    }
}
