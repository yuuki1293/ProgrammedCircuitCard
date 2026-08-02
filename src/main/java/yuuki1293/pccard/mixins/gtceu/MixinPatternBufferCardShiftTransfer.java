package yuuki1293.pccard.mixins.gtceu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;

import yuuki1293.pccard.PCCard;
import yuuki1293.pccard.impl.PatternBufferCardSlotWidget;

@Mixin(value = ModularUIContainer.class, remap = false)
public abstract class MixinPatternBufferCardShiftTransfer {

    @Inject(method = "getShiftClickSlots", at = @At("RETURN"), cancellable = true)
    private void pCCard$prioritizeCardSlot(ItemStack stack, boolean fromContainer,
        CallbackInfoReturnable<List<SlotWidget>> cir) {
        if (!stack.is(PCCard.PROGRAMMED_CIRCUIT_CARD_ITEM.get())) return;

        var slots = cir.getReturnValue();
        if (
            slots.stream()
                .noneMatch(PatternBufferCardSlotWidget.class::isInstance)
        ) return;

        var prioritized = new ArrayList<SlotWidget>(slots.size());
        slots.stream()
            .filter(PatternBufferCardSlotWidget.class::isInstance)
            .forEach(prioritized::add);
        slots.stream()
            .filter(slot -> !(slot instanceof PatternBufferCardSlotWidget))
            .forEach(prioritized::add);
        cir.setReturnValue(prioritized);
    }
}
