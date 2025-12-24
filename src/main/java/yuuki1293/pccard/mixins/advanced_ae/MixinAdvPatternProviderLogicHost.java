package yuuki1293.pccard.mixins.advanced_ae;

import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogic;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;

@Mixin(value = AdvPatternProviderLogicHost.class, remap = false)
public interface MixinAdvPatternProviderLogicHost extends IUpgradeableObject {

    @Shadow
    AdvPatternProviderLogic getLogic();

    default IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) this.getLogic()).getUpgrades();
    }
}
