package se.mickelus.mutil.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

/**
 * Texture with half "pixel" offset
 */
public class GuiTextureOffset extends GuiTexture {

    public GuiTextureOffset(int x, int y, int width, int height, Identifier textureIdentifier) {
        super(x, y, width + 1, height + 1, textureIdentifier);
    }

    public GuiTextureOffset(int x, int y, int width, int height, int textureX, int textureY, Identifier textureIdentifier) {
        super(x, y, width + 1, height + 1, textureX, textureY, textureIdentifier);
    }

    @Override
    public void draw(final GuiGraphicsExtractor graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
        drawChildren(graphics, refX + x, refY + y, screenWidth, screenHeight, mouseX, mouseY, opacity * this.opacity);

        graphics.pose().pushMatrix();
        graphics.pose().translate(0.5F, 0.5F);
        drawTexture(graphics, textureIdentifier, refX + x, refY + y, width - 1, height - 1, textureX, textureY, color,
                getOpacity() * opacity);
        graphics.pose().popMatrix();
    }
}
