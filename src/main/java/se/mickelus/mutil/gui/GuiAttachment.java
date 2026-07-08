package se.mickelus.mutil.gui;

public enum GuiAttachment {
    topLeft,
    topCenter,
    topRight,
    middleLeft,
    middleCenter,
    middleRight,
    bottomLeft,
    bottomCenter,
    bottomRight;

    public GuiAttachment flipHorizontal() {
        return switch (this) {
            case topLeft -> topRight;
            case topRight -> topLeft;
            case middleLeft -> middleRight;
            case middleRight -> middleLeft;
            case bottomLeft -> bottomRight;
            case bottomRight -> bottomLeft;
            default -> this;
        };
    }
}
