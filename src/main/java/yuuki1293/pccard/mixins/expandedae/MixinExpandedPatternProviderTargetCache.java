package yuuki1293.pccard.mixins.expandedae;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.api.storage.MEStorage;
import appeng.helpers.patternprovider.PatternProviderTarget;
import appeng.util.ConfigManager;
import lu.kolja.expandedae.api.patternprovider.PatternProviderTargetCache;
import yuuki1293.pccard.impl.ExpandedPatternProviderTarget;

@Mixin(value = PatternProviderTargetCache.class, remap = false)
public class MixinExpandedPatternProviderTargetCache {

    @Shadow
    @Final
    private ConfigManager configManager;

    @Inject(method = "wrapMeStorage", at = @At("RETURN"), cancellable = true)
    private void pCCard$wrapTarget(MEStorage storage, CallbackInfoReturnable<PatternProviderTarget> cir) {
        var target = cir.getReturnValue();
        if (target != null) {
            cir.setReturnValue(new ExpandedPatternProviderTarget(target, storage, configManager));
        }
    }
}
