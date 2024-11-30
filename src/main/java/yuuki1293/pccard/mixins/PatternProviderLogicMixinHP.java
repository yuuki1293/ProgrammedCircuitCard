package yuuki1293.pccard.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.CompetitionFixer;
import yuuki1293.pccard.Holder;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class PatternProviderLogicMixinHP {
    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Shadow
    public abstract void updatePatterns();

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V", at = @At("TAIL"))
    private void init(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) {
            Holder.callback = () -> {
                this.host.saveChanges();
                updatePatterns();
            };
        }
    }
}
