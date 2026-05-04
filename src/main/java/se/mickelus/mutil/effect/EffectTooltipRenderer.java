package se.mickelus.mutil.effect;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.function.Function;
import java.util.function.Supplier;

public class EffectTooltipRenderer implements IClientMobEffectExtensions {
    private final Function<MobEffectInstance, String> constructEffectTooltip;

    public EffectTooltipRenderer(Function<MobEffectInstance, String> constructEffectTooltip) {
        this.constructEffectTooltip = constructEffectTooltip;
    }

    public static void renderInventoryEffectTooltip(GuiGraphicsExtractor graphics, int x, int y, Supplier<Component> tooltip) {
        Minecraft mc = Minecraft.getInstance();
        Window window = mc.getWindow();

        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();
        int mouseX = (int) (mc.mouseHandler.xpos() * width / window.getScreenWidth());
        int mouseY = (int) (mc.mouseHandler.ypos() * height / window.getScreenHeight());

        if (x < mouseX && mouseX < x + 120 && y < mouseY && mouseY < y + 32) {
            graphics.setTooltipForNextFrame(tooltip.get(), mouseX, mouseY);
        }
    }

    @Override
    public boolean renderInventoryIcon(final MobEffectInstance instance, final AbstractContainerScreen<?> screen,
            final GuiGraphicsExtractor graphics, final int x, final int y, final int blitOffset) {
        renderInventoryEffectTooltip(graphics, x, y, () -> Component.literal(constructEffectTooltip.apply(instance)));
        return false;
    }
}
