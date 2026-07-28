package yuuki1293.pccard.impl;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.EnumSelectorWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;

public enum PatternBufferBlockingMode implements EnumSelectorWidget.SelectableEnum {

    NORMAL("normal", 0),
    SMART("smart", 1),
    FULL("full", 2);

    public static final PatternBufferBlockingMode[] VALUES = values();

    private final String translationKey;
    private final String descriptionKey;
    private final IGuiTexture icon;

    PatternBufferBlockingMode(String name, int textureIndex) {
        this.translationKey = "gui.pccard.pattern_buffer.blocking_mode." + name;
        this.descriptionKey = this.translationKey + ".description";
        this.icon = GuiTextures.DISTRIBUTION_MODE.getSubTexture(0, textureIndex / 3.0f, 1, 1.0f / 3.0f);
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
