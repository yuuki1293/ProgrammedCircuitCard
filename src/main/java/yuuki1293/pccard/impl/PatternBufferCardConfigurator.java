package yuuki1293.pccard.impl;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import appeng.core.localization.GuiText;
import yuuki1293.pccard.PCCard;

public final class PatternBufferCardConfigurator {

    private static final int SLOT_SIZE = 18;
    private static final int PANEL_MARGIN = 2;
    private static final int PANEL_WIDTH = SLOT_SIZE;
    private static final int SLOT_Y = 14;

    private PatternBufferCardConfigurator() {}

    public static void attachUpgradePanel(WidgetGroup root, CustomItemStackHandler inventory) {
        var panel = new WidgetGroup(root.getSizeWidth() + PANEL_MARGIN, 0, PANEL_WIDTH, root.getSizeHeight());
        List<Component> tooltip = List.of(
            GuiText.CompatibleUpgrades.text(),
            GuiText.CompatibleUpgrade.text(
                PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get()
                    .getDescription(),
                1)
                .withStyle(ChatFormatting.GRAY));
        panel.addWidget(
            new SlotWidget(inventory, 0, 0, SLOT_Y).setBackgroundTexture(GuiTextures.SLOT)
                .setHoverTooltips(tooltip));

        root.setSizeWidth(root.getSizeWidth() + PANEL_MARGIN * 2 + PANEL_WIDTH);
        root.addWidget(panel);
    }
}
