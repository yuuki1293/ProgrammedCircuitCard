package yuuki1293.pccard.impl;

import net.minecraft.network.chat.Component;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.FancyInvConfigurator;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import yuuki1293.pccard.PCCard;

public final class PatternBufferCardConfigurator extends FancyInvConfigurator {

    private static final int SLOT_SIZE = 18;
    private static final int PANEL_MARGIN = 4;
    private static final int PANEL_WIDTH = 46;
    private static final int SLOT_X = (PANEL_WIDTH - SLOT_SIZE) / 2;
    private static final int SLOT_Y = 14;

    public PatternBufferCardConfigurator(CustomItemStackHandler inventory, Component title) {
        super(inventory, title);
    }

    public static void attachUpgradePanel(WidgetGroup root, CustomItemStackHandler inventory) {
        var panel = new WidgetGroup(
            root.getSizeWidth() + PANEL_MARGIN,
            0,
            PANEL_WIDTH,
            SLOT_Y + SLOT_SIZE + PANEL_MARGIN);
        panel.addWidget(new LabelWidget(0, 2, Component.translatable("gui.ae2.StorageCellTooltipUpgrades")));
        panel.addWidget(
            new SlotWidget(inventory, 0, SLOT_X, SLOT_Y).setBackgroundTexture(GuiTextures.SLOT)
                .setOnAddedTooltips((ignored, tooltip) -> {
                    tooltip.add(Component.translatable("gui.pccard.pattern_buffer.card.tooltip"));
                    tooltip.add(Component.translatable("gui.pccard.pattern_buffer.card.locked"));
                }));

        root.setSizeWidth(root.getSizeWidth() + PANEL_MARGIN * 2 + PANEL_WIDTH);
        root.addWidget(panel);
    }

    @Override
    public IGuiTexture getIcon() {
        return new ItemStackTexture(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get()).scale(0.8f);
    }
}
