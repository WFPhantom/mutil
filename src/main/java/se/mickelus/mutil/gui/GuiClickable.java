package se.mickelus.mutil.gui;

public class GuiClickable extends GuiElement {

    protected final Runnable onClickHandler;

    public GuiClickable(int x, int y, int width, int height, Runnable onClickHandler) {
        super(x, y, width, height);

        this.onClickHandler = onClickHandler;
    }

    @Override
    public boolean onMouseClick(int x, int y, int button) {
        if (hasFocus()) {
            onClickHandler.run();
            return true;
        }

        return false;
    }
}
