package yuuki1293.pccard.impl;

import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

/**
 * Marker widget used to prioritize the dedicated card slot during shift-transfer.
 */
public final class PatternBufferCardSlotWidget extends SlotWidget {

    public PatternBufferCardSlotWidget(CustomItemStackHandler inventory, int slot, int x, int y) {
        super(inventory, slot, x, y);
    }
}
