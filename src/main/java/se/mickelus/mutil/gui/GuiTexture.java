package se.mickelus.mutil.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class GuiTexture extends GuiElement {

    protected Identifier textureIdentifier;

    protected int textureWidth = 256;
    protected int textureHeight = 256;
    protected int textureX;
    protected int textureY;

    protected int color = 0xffffff;

    public GuiTexture(int x, int y, int width, int height, Identifier textureIdentifier) {
        this(x, y, width, height, 0, 0, textureIdentifier);
    }

    public GuiTexture(int x, int y, int width, int height, int textureX, int textureY, Identifier textureIdentifier) {
        super(x, y, width, height);

        this.textureX = textureX;
        this.textureY = textureY;

        this.textureIdentifier = textureIdentifier;
    }

    public GuiTexture setTextureCoordinates(int x, int y) {
        textureX = x;
        textureY = y;
        return this;
    }

    public GuiTexture setColor(int color) {
        this.color = color;
        return this;
    }

    public GuiTexture setSpriteSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    @Override
    public void draw(final GuiGraphicsExtractor graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY,
            float opacity) {
        super.draw(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);

        drawTexture(graphics, textureIdentifier, refX + x, refY + y, width, height, textureX, textureY, color, getOpacity() * opacity);
    }

    protected void drawTexture(final GuiGraphicsExtractor graphics, Identifier textureIdentifier, int x, int y, int width, int height,
                               int u, int v, int color, float opacity) {
        int argb = colorWithOpacity(color, opacity);
        graphics.blit(RenderPipelines.GUI_TEXTURED, textureIdentifier, x, y, u, v, width, height, textureWidth, textureHeight, argb);
    }
}
