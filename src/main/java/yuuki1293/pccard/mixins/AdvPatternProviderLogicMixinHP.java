package yuuki1293.pccard.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.UpgradeInventories;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.xmod.CompetitionFixer;
import yuuki1293.pccard.PCCard;

@Mixin(value = AdvPatternProviderLogic.class, remap = false, priority = 1200)
abstract public class AdvPatternProviderLogicMixinHP {
    @Shadow
    public abstract void updatePatterns();

    @Shadow
    @Final
    private AdvPatternProviderLogicHost host;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lnet/pedroksl/advanced_ae/common/logic/AdvPatternProviderLogicHost;I)V", at = @At("TAIL"))
    private void init(IManagedGridNode mainNode, AdvPatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        if (CompetitionFixer.existAppflux.get()) {
            try {
                @SuppressWarnings("JavaReflectionMemberAccess")
                var upgradeField = AdvPatternProviderLogic.class.getDeclaredField(CompetitionFixer.appFluxUpgradeField);
                upgradeField.setAccessible(true);
                upgradeField.set(this, UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 2, this::pCCardHP$upgradesChange));
            } catch (Exception e) {
                PCCard.LOGGER.error("Can't write field", e);
            }
        }
    }

    @Unique
    private void pCCardHP$upgradesChange() {
        this.host.saveChanges();
        updatePatterns();
    }
}
