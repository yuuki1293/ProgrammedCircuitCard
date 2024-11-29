package yuuki1293.pccard;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import org.spongepowered.asm.mixin.Unique;

public interface IPatternProviderMenuMixin {
    @Unique
    void pCCard$setupUpgrades();

    @Unique
    IUpgradeableObject pCCard$getHost();

    @Unique
    IUpgradeInventory pCCard$getUpgrades();
}
