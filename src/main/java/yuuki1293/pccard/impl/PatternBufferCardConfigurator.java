package yuuki1293.pccard.impl;

import net.minecraft.network.chat.Component;

import com.gregtechceu.gtceu.api.machine.fancyconfigurator.FancyInvConfigurator;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;

import yuuki1293.pccard.PCCard;

public final class PatternBufferCardConfigurator extends FancyInvConfigurator {

    public PatternBufferCardConfigurator(CustomItemStackHandler inventory, Component title) {
        super(inventory, title);
    }

    @Override
    public IGuiTexture getIcon() {
        return new ItemStackTexture(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get()).scale(0.8f);
    }
}
