package yuuki1293.pccard.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.execution.CraftingCpuLogic;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yuuki1293.pccard.IPatternProviderLogicMixin;

@Mixin(value = CraftingCpuLogic.class, remap = false)
public abstract class CraftingCPULogicMixin {
    @Redirect(method = "executeCrafting", at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingProvider;pushPattern(Lappeng/api/crafting/IPatternDetails;[Lappeng/api/stacks/KeyCounter;)Z"))
    private boolean pushPattern(ICraftingProvider instance, IPatternDetails patternDetails, KeyCounter[] keyCounters, @Local ICraftingProvider provider) {
        if (provider.pushPattern(patternDetails, keyCounters)) {
            if (provider instanceof IPatternProviderLogicMixin logicMixin)
                logicMixin.pCCard$setPCNumber(patternDetails);
            return true;
        } else return false;
    }
}
