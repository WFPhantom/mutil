package se.mickelus.mutil.gui;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class GuiRoot extends GuiElement {

    protected Minecraft mc;

    public GuiRoot(Minecraft mc) {
        super(0, 0, 0 ,0);
        this.mc = mc;
    }

    public void draw(GuiGraphicsExtractor graphics) {
        if (isVisible()) {
            Window window = mc.getWindow();

            width = window.getGuiScaledWidth();
            height = window.getGuiScaledHeight();
            double mouseX = mc.mouseHandler.xpos() * width / window.getScreenWidth();
            double mouseY = mc.mouseHandler.ypos() * height / window.getScreenHeight();

            drawChildren(graphics, 0, 0, width, height, (int) mouseX, (int) mouseY, 1);
        }
    }
}