package yuuki1293.pccard.impl;

import com.gregtechceu.gtceu.api.gui.widget.EnumSelectorWidget;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;

public enum PatternBufferBlockingMode implements EnumSelectorWidget.SelectableEnum {

    DISABLED("gui.pccard.pattern_buffer.blocking_mode.off", "gui.pccard.pattern_buffer.blocking_mode.off.description",
        80, 16),
    NORMAL("gui.expandedae.blocking_mode.Default", "gui.expandedae.blocking_mode.default", 80, 16),
    SMART("gui.expandedae.blocking_mode.Smart", "gui.expandedae.blocking_mode.smart", 64, 16),
    FULL("gui.expandedae.blocking_mode.Full", "gui.expandedae.blocking_mode.all", 96, 0);

    public static final PatternBufferBlockingMode[] VALUES = values();
    public static final PatternBufferBlockingMode[] BASIC_VALUES = { DISABLED, NORMAL };

    private final String translationKey;
    private final String descriptionKey;
    private final IGuiTexture icon;

    PatternBufferBlockingMode(String translationKey, String descriptionKey, int textureX, int textureY) {
        this.translationKey = translationKey;
        this.descriptionKey = descriptionKey;
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
