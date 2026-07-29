package yuuki1293.pccard.impl;

import com.gregtechceu.gtceu.api.gui.widget.EnumSelectorWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

public enum PatternBufferBlockingMode implements EnumSelectorWidget.SelectableEnum {

    NORMAL("normal", 80, 16),
    SMART("smart", 64, 16),
    FULL("full", 96, 0);

    public static final PatternBufferBlockingMode[] VALUES = values();

    private final String translationKey;
    private final String descriptionKey;
    private final IGuiTexture icon;

    PatternBufferBlockingMode(String name, int textureX, int textureY) {
        this.translationKey = "gui.pccard.pattern_buffer.blocking_mode." + name;
        this.descriptionKey = this.translationKey + ".description";
        this.icon = ae2Icon(textureX, textureY);
    }

    private static IGuiTexture ae2Icon(int x, int y) {
        return new ResourceTexture("ae2:textures/guis/states.png")
            .getSubTexture(x / 256.0f, y / 256.0f, 16 / 256.0f, 16 / 256.0f);
    }

    public String translationKey() {
        return translationKey;
    }

    public String descriptionKey() {
        return descriptionKey;
    }

    @Override
    public String getTooltip() {
        return translationKey;
    }

    @Override
    public IGuiTexture getIcon() {
        return icon;
    }
}
