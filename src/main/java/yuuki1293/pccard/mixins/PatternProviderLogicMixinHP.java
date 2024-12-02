package yuuki1293.pccard.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yuuki1293.pccard.CompetitionFixer;
import yuuki1293.pccard.PCCard;

@Mixin(value = PatternProviderLogic.class, remap = false, priority = 1200)
public abstract class PatternProviderLogicMixinHP {
    @Shadow
    public abstract void updatePatterns();

    @Shadow
    @Final
    private PatternProviderLogicHost host;

    @Inject(method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V", at = @At("TAIL"))
    private void init(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        if (CompetitionFixer.hasPatternProviderUpgrade()) {
            try {
                var upgradeField = PatternProviderLogic.class.getDeclaredField("af_$upgrades");
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
