package yuuki1293.pccard.mixins.advanced_ae;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;
import net.pedroksl.advanced_ae.common.logic.AdvCraftingCPULogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yuuki1293.pccard.wrapper.IPatternProviderLogicMixin;

@Mixin(value = AdvCraftingCPULogic.class, remap = false)
public class MixinAdvCraftingCPULogic {
    @Redirect(
            method = "executeCrafting",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/api/networking/crafting/ICraftingProvider;pushPattern(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;)Z"))
    private boolean pushPattern(ICraftingProvider provider, IPatternDetails patternDetails, KeyCounter[] keyCounters) {
        if (provider.pushPattern(patternDetails, keyCounters)) {
            if (provider instanceof IPatternProviderLogicMixin logicMixin)
                logicMixin.pCCard$setPCNumber(patternDetails);
            return true;
        } else return false;
    }
}
